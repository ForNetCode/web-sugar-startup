### Web Starter
#### SPA
基于 Vite + Typescript + React 构建项目：
```shell
# https://vitejs.dev/guide/  init react-ts project
npm create vite@latest frontend -- --template react-ts

# https://github.com/pmndrs/zustand  zustand 状态管理
npm install --save zustand

# https://tanstack.com/ TanStack Router 采用file base 录用
npm i --save @tanstack/react-router @tanstack/router-devtools
npm install --save-dev @tanstack/router-vite-plugin

# https://ui.shadcn.com/docs/installation/vite shadcn/ui tailwind UI库

# https://react-hook-form.com/get-started https://zod.dev/?id=requirements  react-hook-form and zod 表单和校验
npm install --save react-hook-form zod

# https://github.com/pimterry/loglevel loglevel 日志
npm install --save loglevel
```
`vite.config.ts` 此仅供参考
```typescript
/// <reference types="vitest" />
import { defineConfig } from 'vite'
import path from "path"
import react from '@vitejs/plugin-react'
import { TanStackRouterVite } from '@tanstack/router-vite-plugin'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
      react(),
      TanStackRouterVite(),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    //environment: 'jsdom',
    include:["./test/**/*.test.ts"],
  },
  base: './',
})
```

基于 Vite + Typescript + Refine + Ant Design 构建项目：
参考：refine.dev/docs/getting-started/quickstart/#using-browser
项目结构基本都有了，可以用来快速构建简易项目。
```shell
npm install --save loglevel zustand
npm install --save-dev dotenv-cli cross-var
```

#### spa-server integration
##### install npm packages
```shell
npm install --save-dev cross-var spa-client
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
docker run -d --network=host -v $(pwd)/config.toml:/config/config.toml  -v $(pwd)/data:/server/data --name=spa-server ghcr.nju.edu.cn/fornetcode/spa-server:v2.4.0
```

#### SSR
有两种解决方案，一种走 NextJS，一种走Vite。

Vite 的方案是通过条件让用户自行决定，哪些走Server端，哪些走客户端，同时提供SPA的开发体验，简单、粗暴、快捷。

NextJS提供大量的优化功能，例如字体预处理等，但复杂度高，耦合性高，要用大量它的组件，技术选型和 NextJS强绑定。

若追求极致，还是要走NextJS的方案。


#### APM
APM 一般直接采用云平台或第三方。常见的独立第三方有FundBugs，自建的话，考虑到服务器成本，不划算。
