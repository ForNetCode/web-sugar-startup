### 访问外网API代理
解决方案参考： https://github.com/tech-shrimp/deno-api-proxy 
以下摘抄防失联：

#### Deno部署（推荐）
登录/注册 https://dash.deno.com/account/projects
点击右上角的New Playground
把main.ts里面所有代码复制进去
点击 Save & Deploy
查看部署后的服务的域名（长这样：xxx.deno.dev）
将域名，需要代理的服务名字，API Key填入下方命令中


deno.json
```json
{
  "tasks": {
    "dev": "deno run --watch main.ts"
  },
  "imports": {
    "@std/assert": "jsr:@std/assert@1"
  }
}
```
main.ts
```typescript
async function handleRequest(request: Request): Promise<Response> {

  const url = new URL(request.url);
  const pathname = url.pathname;

  if (pathname === '/' || pathname === '/index.html') {
    return new Response('Proxy is Running！Details：https://github.com/tech-shrimp/deno-api-proxy', {
      status: 200,
      headers: { 'Content-Type': 'text/html' }
    });
  } 
  
  const targetUrl = `https://${pathname}`;

  try {
    const headers = new Headers();
    const allowedHeaders = ['accept', 'content-type', 'authorization'];
    for (const [key, value] of request.headers.entries()) {
      if (allowedHeaders.includes(key.toLowerCase())) {
        headers.set(key, value);
      }
    }

    const response = await fetch(targetUrl, {
      method: request.method,
      headers: headers,
      body: request.body
    });

    const responseHeaders = new Headers(response.headers);
    responseHeaders.set('Referrer-Policy', 'no-referrer');

    return new Response(response.body, {
      status: response.status,
      headers: responseHeaders
    });

  } catch (error) {
    console.error('Failed to fetch:', error);
    return new Response('Internal Server Error', { status: 500 });
  }
};

Deno.serve(handleRequest); 
```

### 流量转发本地
#### 有外网IP服务器
1. 基于 [rathole](https://github.com/rapiz1/rathole) 做转发
2. 基于 zerotier、tailscale 跨网组网，再配上 tiny proxy 或 ssh 做转发。

##### 本地端口转发（Local Port Forwarding）
`ssh -L [本地端口]:[远程服务器地址]:[远程端口] [用户名]@[远程服务器]`
##### 远程端口转发（Remote Port Forwarding）
`ssh -R [远程端口]:[本地地址]:[本地端口] [用户名]@[远程服务器]`

#### 无外网IP
走 Cloudflare Zero Trust Tunnel.
参考： https://zhuanlan.zhihu.com/p/621870045