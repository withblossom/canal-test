package com.heima.item.canal;

import com.github.benmanes.caffeine.cache.Cache;
import com.heima.item.config.RedisHandler;
import com.heima.item.pojo.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Component
@CanalTable("tb_item")
@Slf4j
public class CanalHandler implements EntryHandler<Item> {

    private final RedisHandler redisHandler;
    private final Cache<Long, Item> itemCache;
    public CanalHandler(RedisHandler redisHandler, Cache<Long, Item> itemCache){
        this.itemCache = itemCache;
        this.redisHandler = redisHandler;
    }
    @Override
    public void insert(Item item) {
        log.debug("插入缓存更新,{}",item.toString());
        redisHandler.saveItem(item);
        itemCache.put(item.getId(),item);
    }

    @Override
    public void update(Item before, Item after) {
        log.debug("更新缓存更新{} -> {}",before,after);
        redisHandler.saveItem(after);
        itemCache.put(after.getId(),after);
    }

    @Override
    public void delete(Item item) {
        log.debug("删除缓存更新，{}",item.toString());
        redisHandler.deleteItemById(item.getId());
        itemCache.invalidate(item.getId());
    }
}
