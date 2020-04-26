#!/bin/bash

resetDocker() {
  local containers=$(docker container ls -a | grep 'text-counter-app\|text-counter-kafka\|text-counter-zookeeper')

  if [ -n "$containers" ]; then
    echo "------------------------ [Removing already running containers & dangling images...] ------------------------"
    docker-compose down --volumes
    docker image prune -f
  fi
}

setupDocker() {
  echo "------------------------ [Starting application containers now ...] ------------------------"
  docker-compose up --build
}

displayDockerInfo() {
  echo "------------------------ [Listing containers ...] ------------------------"
  docker ps
}

resetDocker
setupDocker
displayDockerInfo
