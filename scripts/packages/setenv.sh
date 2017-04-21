
# Set JAVA_HOME. Must be at least 1.7.
# If not set, will try to lookup a correct version.
# JAVA_HOME=/some/place/where/to/find/java

# Set the log configuration file
JOPTS="$JOPTS -Dlog4j.configuration=file:/etc/jdchive/log4j.xml"


# You may have to edit this line to adjust to your environement. Provide a space separated list of folders.
# The launching script will try to use one of these folder to find the Hive jars.
CANDIDATE_HIVE_LIBS="/usr/hdp/current/hive-client/lib"

# We need to explicitly add the hive-site config file. Location may vary per configuration
HIVE_CONFIG_FILE='/etc/hive/conf/hive-site.xml'
OPTS="$OPTS --configFile $HIVE_CONFIG_FILE"


# Dump configuration, for debugging
#OPTS="$OPTS --dumpConfigFile /tmp/jdchive-conf.txt"

# Report performed opération
OPTS="$OPTS --reportFile /tmp/jdchive-report-$(date +%y%m%d-%H%M%S).yml"


# Set kerberos principal and keytab (If kerberos is activated and you don't like 'kinit')
# OPTS="$OPTS --principal hiveUser --keytab /home/hiveUser/.keytabs/hiveUser.keytab


