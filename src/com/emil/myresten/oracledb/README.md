podman pull docker.io/gvenzl/oracle-free:23.5-slim-faststart

podman machine set --memory 4096

podman run --rm -p 1521:1521 -e ORACLE_PASSWORD="password" gvenzl/oracle-free:23.5-slim-faststart
