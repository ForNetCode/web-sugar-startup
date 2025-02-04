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
生成出来一整套，包括 axios，不算好用。

### Typescript Interface 代码生成
参考： https://openapi-ts.dev/advanced 只生成 interface
```json
{
  "scripts": {
    "api": "openapi-typescript http://127.0.0.1:8080/docs/docs.yaml --enum --enum-values --dedupe-enums --root-types  -o src/api/schema.d.ts"
  }
}
```

### gRPC Web Client 代码生成 
参考：https://github.com/Aymeric-Henry/GRPC-Vite-TS-Svelte
