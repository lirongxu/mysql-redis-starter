package com.lemon.demo.start;

import com.lemon.demo.config.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @Author lemon
 * @Date 2021/8/19
 */
@Slf4j
@Component
public class StartJob {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private FileConfig fileConfig;
    
    @PostConstruct
    public void startJob() throws IOException {

        String filePath = fileConfig.getPath();
        File destFile = new File(filePath);
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        Cursor<String> cursor = scan(redisTemplate, "*", 1000);
        log.info("======================");
        while (cursor.hasNext()) {
            String key = cursor.next();

            Long expire = redisTemplate.getExpire(key);
            if(expire == -1) {
                // 表示key没有设置ttl；需要删除 先记录下来
                //writeByOutputStreamWrite(filePath, key+"\n");
                log.info(key);
            }
        }
        log.info("======================");
    }

    public static void writeByOutputStreamWrite(String _sDestFile, String _sContent) throws IOException {
        OutputStreamWriter os = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_sDestFile);
            os = new OutputStreamWriter(fos, "UTF-8");
            os.write(_sContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (os != null) {
                os.close();
                os = null;
            }
            if (fos != null) {
                fos.close();
                fos = null;
            }

        }
    }


    public Cursor<String> scan(RedisTemplate redisTemplate, String pattern, int limit) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        return (Cursor) redisTemplate.executeWithStickyConnection(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize);
            }
        });
    }
}
