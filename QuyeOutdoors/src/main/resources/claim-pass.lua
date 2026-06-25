---@diagnostic disable: undefined-global

local passId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

local stockKey = 'quye:flash:stock:' .. passId
local orderKey = 'quye:pass:order:' .. passId

if tonumber(redis.call('GET', stockKey)) <= 0 then
    return 1
end

if redis.call('SISMEMBER', orderKey, userId) == 1 then
    return 2
end

redis.call('INCRBY', stockKey, -1)

redis.call('SADD', orderKey, userId)


return 0