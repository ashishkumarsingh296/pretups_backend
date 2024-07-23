#!/bin/bash

#picking the pid in current directory stored from bootup.sh
kill $(cat pid.file)
