#!/bin/sh
docker run --name stepserv -p 8080:8080 --rm --env JWT_SECRET=supersecret thinkboxberlin/stepserv
