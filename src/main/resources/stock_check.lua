-- stock_key=redis的key值，用来获取数量
local stock_key = KEYS[1]
-- 数量
local quantity = tonumber(ARGV[1])
-- 现在的时间，tonumber将字符串转换为数字，方便比较
local now = tonumber(ARGV[2])
-- 过期时间
local expire_time = tonumber(ARGV[3])
-- 1. 过期校验 (只有设置了过期时间才判断)
if expire_time > 0 and now > expire_time then
    return -3
end

-- 2. 库存校验
local current_stock = redis.call('get', stock_key)
if not current_stock then
    return -1
end

if tonumber(current_stock) < quantity then
    return 0
end

-- 3. 原子扣减
redis.call('decrby', stock_key, quantity)
return 1