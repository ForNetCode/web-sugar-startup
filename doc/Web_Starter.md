### Web Starter

```shell
# https://vitejs.dev/guide/  create react-ts project
npm create vite@latest my-vue-app -- --template react-ts

# https://tailwindcss.com/docs/guides/vite  install tailwind

```

#### spa-server integration
##### install npm packages
```shell
npm install --save-dev dotenv-cli cross-var spa-client
```
`.env` config
```ini
# all config start with `SPA` for spa-client

# ssh -N -L 9000:server:9000 -p jumpPort  root@jumpServer

SPA_SERVER_ADDRESS=http://127.0.0.1:9000

SPA_SERVER_AUTH_TOKEN=token

# default is 3
SPA_UPLOAD_PARALLEL=3

DOMAIN=www.example.com

```

`package.json`
```json

{
  "scripts": {
    "upload": "dotenv cross-var spa-client upload ./dist %DOMAIN%",
    "release": "dotenv cross-var spa-client release %DOMAIN%",
    "deploy": "npm run build && npm run upload && npm run release"
  }
}
```


##### deploy spa-server
`config.toml`
```toml
file_dir = "/server/data"
cors = true # 必须有

[http]
port = 8080
addr = "0.0.0.0"

[admin_config]
port = 9000
addr = "0.0.0.0"
token = "admin_token"
```

```shell
docker run -d --network=host  -v $(pwd)/config.toml:/config/config.toml  -v $(pwd)/data:/server/data --name=spa-server ghcr.nju.edu.cn/fornetcode/spa-server:v2.4.0
```