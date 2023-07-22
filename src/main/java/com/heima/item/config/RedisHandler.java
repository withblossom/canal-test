package com.heima.item.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.service.IItemService;
import com.heima.item.service.IItemStockService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisHandler implements InitializingBean {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IItemService itemService;
    @Autowired
    private IItemStockService iItemStockService;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Item> itemList = itemService.list();
        for (Item item:itemList){
            String itemJson = MAPPER.writeValueAsString(item);
            stringRedisTemplate.opsForValue().set("item:id:"+item.getId(),itemJson);
        }

        List<ItemStock> itemStockList = iItemStockService.list();
        for (ItemStock itemStock:itemStockList){
            String itemJson = MAPPER.writeValueAsString(itemStock);
            stringRedisTemplate.opsForValue().set("item:stock:id:"+itemStock.getId(),itemJson);
        }
    }
    public void saveItem(Item item){
        try {
            String itemJson = MAPPER.writeValueAsString(item);
            stringRedisTemplate.opsForValue().set("item:id:"+item.getId(),itemJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
    public void deleteItemById(Long id){
        stringRedisTemplate.delete("item:id:"+id);
    }
}
