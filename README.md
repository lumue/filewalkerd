# Filewalkerd

## API

### example http requests 

```
curl -X POST --location "http://vm-services-home:8020/scan" \
    -H "Content-Type: application/json" \
    -d '{"path": "/media/adult/2024/Januar", "fileHandlerId":"scanners.CollectAndWriteMovieMetadataTask"}'
```
or
```
POST http://vm-services-home:8020/scan
Content-Type: application/json

{"path": "/media/adult/2024/Januar", "fileHandlerId":"scanners.CollectAndWriteMovieMetadataTask"}
```