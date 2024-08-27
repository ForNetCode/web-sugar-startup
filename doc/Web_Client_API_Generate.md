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

### gRPC Web Client 代码生成 
参考：https://github.com/Aymeric-Henry/GRPC-Vite-TS-Svelte




