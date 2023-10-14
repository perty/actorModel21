#!/usr/bin/env bash

(cd target && java -cp "actorModel21-1.0-SNAPSHOT.jar" se.artcomputer.edu.actor.chat.ChatClient $1)
