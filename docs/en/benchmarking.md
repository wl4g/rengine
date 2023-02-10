# Rengine for Benchmarking

## Executor

- for example:

```bash
# Prepare parameters.
cat <<EOF > /tmp/post.json
{
  "requestId": "b9bc3e0e-d705-4ff2-9edf-970dcf95dea5",
  "clientId": "JVqEpEwIaqkEkeD5",
  "clientSecret": "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77",
  "scenesCodes": ["test_script_sdk_example"],
  "timeout": 30000,
  "bestEffort": true,
  "args": {
    "userId": "u10010101",
    "foo": "bar"
  }
}
EOF

# Simulate concurrencys.
ab -p /tmp/post.json -T application/json -n 1000 -c 100 http://localhost:28002/execution/execute/internal

# Analyzing error logs.
export LOG_PATH="/mnt/disk1/log/rengine-executor/rengine-executor.log"
echo > $LOG_PATH; tail -F $LOG_PATH | jq -r '.level="ERROR"' > /tmp/error.log
```

- TODO
