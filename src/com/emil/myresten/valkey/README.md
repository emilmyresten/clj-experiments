Valkey is a fork of Redis with good license.
https://valkey.io/topics/introduction/

Default port 6379.

Almost all docs from redis apply to valkey.
https://redis.io/docs/latest/develop/get-started/

brew install valkey (for valkey-cli)

podman run --rm -p 6379:6379 docker.io/valkey/valkey:latest

Valkey takes requests over tcp.

SET <key> <value>
GET <key>

Getting non-existing key returns nil.

SETEX to set key with ttl.
SETEX <key> <seconds> <val>

keys are often namespaced with colon like
user:1000:username


Can use regular redis client libraries. Using Jedis in this case. its MIT licensed. 