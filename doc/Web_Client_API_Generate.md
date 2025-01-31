### OpenAPI Axios 代码生成
参考：https://github.com/OpenAPITools/openapi-generator-cli

```shell
npm install --save-dev @openapitools/openapi-generator-cli
```
package.json
```json
{
  "scripts": {
    "generate-api": "openapi-generator-cli generate -i http://127.0.0.1:8080/docs/docs.yaml -g typescript-axios -o generated-sources/openapi"
  }
}
```
生成出来一整套，包括 axios，不算好用。还是需要针对性做处理

### Typescript Interface 代码生成
参考： https://openapi-ts.dev/advanced

### gRPC Web Client 代码生成 
参考：https://github.com/Aymeric-Henry/GRPC-Vite-TS-Svelte
