#!/bin/bash
# Hadoop version - 3.3.6
# Java   version - openjdk 21.0.2

# Build JAR file that will be located at ./target/hadoop-language-detector-1.0-SNAPSHOT.jar
mvn clean package
# Copy generated jar to the root folder for convenience. Optional step.
mv target/hadoop-language-detector-1.0-SNAPSHOT.jar language-detector.jar

# Train model with input files.
# There are two files with English [english.txt] and Dutch [dutch.txt] texts.
# Following commands generate Language profiles in ./src/main/resources/lang directory:
#   * ./src/main/resources/lang/english - English Language Profile file.
#   * ./src/main/resources/lang/dutch   - Dutch Language Profile file.
#
# Usage: hadoop jar <app.jar> <train-file> <output-directory> train
#   * <app.jar>          - Path to application jar, build by 'mvn package'.
#   * <train-file>       - Path to input file with sentences for Language Training.
#   * <output-directory> - Output directory for Training results.
#   * train              - Language Training Mode.
#                          This argument is mandatory for Training mode.
hadoop jar language-detector.jar dutch.txt src/main/resources/ train
hadoop jar language-detector.jar english.txt src/main/resources/ train

# Try detecting languages.
# There are two files with words:
#   * guess-dutch.txt          - only Dutch sentences.
#   * guess-english+dutch.txt  - English + Dutch sentences, to verify that model correctly resolves different languages.
#
# The commands generate Detection Results in ./src/main/resources directory.
# Filenames would be alike 'detection-results-2023-09-03T11:58:31.115828Z.txt'.
#
# Usage: hadoop jar <app.jar> <guess-file> <output-directory> [detect]
#   * <app.jar>          - Path to application jar, build by 'mvn package'.
#   * <guess-file>       - Path to input file with sentences for Language Detection.
#   * <output-directory> - Output directory for detection results.
#                          Must be the same where Language Profiles directory is located.
#   * [detect]           - Language Detection Mode.
#                          This argument is optional here as it's the default.
hadoop jar language-detector.jar guess-dutch.txt src/main/resources/ detect
hadoop jar language-detector.jar guess-english+dutch.txt src/main/resources/
