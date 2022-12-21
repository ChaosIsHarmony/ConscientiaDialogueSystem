#!/bin/bash

cd bin/
java -cp ../libs/gson-2.8.8.jar: cds.Main "../resources/config.json"
