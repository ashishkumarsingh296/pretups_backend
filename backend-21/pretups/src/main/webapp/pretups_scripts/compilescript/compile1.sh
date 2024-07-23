source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/alepokenyastub.jar:.

export BASH_ENV PATH CLASSPATH

cd /pretupshome/tomcat5_web/webapps/pretups/WEB-INF/src/
echo "Your CLASSPATH is "$CLASSPATH

echo compileing btsl.common
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/common/*.java

echo compileing btsl.logging
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/logging/*.java

echo compileing btsl.logging.impl
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/logging/impl/*.java

echo compileing btsl.pretups.common
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/common/*.java

echo compileing btsl.pretups.interfaces.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/interfaces/businesslogic/*.java

echo compileing btsl..pretups.interfaces.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/interfaces/web/*.java

echo compileing btsl.pretups.logging
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/logging/*.java

echo compileing btsl.pretups.channel.logging
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/logging/*.java

echo compileing btsl.pretups.p2p.logging
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/logging/*.java

echo compileing btsl.pretups.master.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/master/businesslogic/*.java

echo compileing btsl.pretups.master.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/master/web/*.java

echo compileing btsl.pretups.network.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/network/businesslogic/*.java

echo compileing btsl.pretups.network.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/network/web/*.java

echo compileing btsl.pretups.p2p.receiver.requesthandler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/receiver/requesthandler/*.java

echo compileing btsl.pretups.p2p.subscriber.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/subscriber/businesslogic/*.java

echo compileing btsl.pretups.p2p.subscriber.requesthandler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/subscriber/requesthandler/*.java

echo compileing btsl.pretups.p2p.subscriber.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/subscriber/web/*.java

echo compileing btsl.pretups.p2p.transfer.requesthandler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/transfer/requesthandler/*.java

echo compileing btsl.pretups.p2p.transfer.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/transfer/businesslogic/*.java


echo compileing btsl.pretups.p2p.query.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/query/businesslogic/*.java

echo compileing btsl.pretups.p2p.query.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/query/web/*.java

echo compileing btsl.pretups.servicekeyword.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/servicekeyword/businesslogic/*.java

echo compileing btsl.pretups.servicekeyword.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/servicekeyword/web/*.java

echo compileing btsl.pretups.servicekeyword.requesthandler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/servicekeyword/requesthandler/*.java

#echo compileing btsl.pretups.user
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/user/*.java

echo compling com.btsl.pretups.user.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/user/web/*.java

echo compileing btsl.pretups.util
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/util/*.java

echo compileing btsl.xl
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/xl/*.java

#echo compileing btsl.pretups.transfer.requesthandler
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/transfer/requesthandler/*.java

echo compileing btsl.pretups.transfer.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/transfer/businesslogic/*.java

echo compileing btsl.pretups.transfer.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/transfer/web/*.java

echo compileing btsl.pretups.preference.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/preference/businesslogic/*.java

echo compileing btsl.pretups.preference.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/preference/web/*.java

echo compileing btsl.pretups.receiver
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/receiver/*.java

echo compileing btsl.pretups.skey.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/skey/businesslogic/*.java

echo compileing btsl.pretups.payment.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/payment/businesslogic/*.java

echo compileing btsl.pretups.subscriber.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/subscriber/businesslogic/*.java

echo compileing btsl.pretups.subscriber.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/subscriber/web/*.java

echo compileing btsl.pretups.inter.cache
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cache/*.java

echo compileing btsl.pretups.inter.connection
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/connection/*.java

echo compileing btsl.pretups.inter.ericssion
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/ericssion/*.java

echo compileing btsl.pretups.inter.module
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/module/*.java

echo compileing btsl.pretups.cardgroup.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/cardgroup/businesslogic/*.java

echo compileing btsl.pretups.cardgroup.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/cardgroup/web/*.java

echo compileing btsl.pretups.gateway.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/gateway/businesslogic/*.java

echo compileing btsl.pretups.gateway.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/gateway/web/*.java

echo compileing btsl.util
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/util/*.java

echo compileing btsl.alaram
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/alarm/*.java

#echo compileing btsl.category
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/category/*.java

echo compileing btsl.event
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/event/*.java

echo compileing btsl.loadcontroller
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/loadcontroller/*.java

echo compileing btsl.login
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/login/*.java

echo compileing btsl.menu
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/menu/*.java

#echo compileing btsl.user
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/user/*.java

echo compileing btsl.user.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/user/businesslogic/*.java

echo compileing btsl.user.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/user/web/*.java

echo compileing btsl.session.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/session/businesslogic/*.java

echo compileing btsl.session.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/session/web/*.java

echo compileing btsl.pretups.iccidkeymgmt.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/iccidkeymgmt/web/*.java

echo compileing btsl.pretups.iccidkeymgmt.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/iccidkeymgmt/businesslogic/*.java

echo compileing btsl.ota.util
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/util/*.java

echo compileing btsl.ota.generator
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/generator/*.java

echo compileing btsl.ota.services.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/services/businesslogic/*.java

echo compileing btsl.ota.services.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/services/web/*.java


echo compileing btsl.ota.bulkpush.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/bulkpush/businesslogic/*.java

echo compileing btsl.ota.bulkpush.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/ota/bulkpush/web/*.java


echo compileing btsl.pretups.channel.transfer.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/web/*.java

echo compileing btsl.pretups.channel.transfer.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/businesslogic/*.java

echo compileing btsl.pretups.networkstock.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/networkstock/web/*.java

echo compileing btsl.pretups.networkstock.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/networkstock/businesslogic/*.java

echo compileing btsl.pretups.channel.user.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/user/web/*.java

echo compileing btsl.pretups.channel.user.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/user/businesslogic/*.java

echo compileing btsl.pretups.channel.transfer.requesthandler.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/requesthandler/*.java

echo compileing btsl.pretups.domain.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/domain/web/*.java

echo compileing btsl.pretups.domain.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/domain/businesslogic/*.java

echo compiling btsl.pretups.channel.profile.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/profile/businesslogic/*.java

echo compiling btsl.pretups.channel.profile.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/profile/web/*.java

echo compiling btsl.pretups.channel.userreturn.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/userreturn/web/*.java

echo compiling btsl.pretups.channel.receiver
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/receiver/*.java

echo compiling btsl.pretups.p2p.reports.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/reports/businesslogic/*.java

echo compiling btsl.pretups.p2p.reports.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/reports/web/*.java

echo compiling btsl.pretups.p2p.transfer.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/transfer/businesslogic/*.java

echo compiling btsl.pretups.product.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/product/businesslogic/*.java

echo compiling btsl.pretups.product.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/product/web/*.java

echo compiling btsl.pretups.roles.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/roles/businesslogic/*.java

echo compiling btsl.pretups.roles.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/roles/web/*.java

echo compiling btsl.pretups.stk
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/stk/*.java

echo compiling btsl.pretups.user.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/user/businesslogic/*.java

echo compiling com.btsl.pretups.channel.reports.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/reports/web/*.java

echo compiling com.btsl.pretups.channel.reports.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/reports/businesslogic/*.java

echo compiling com.btsl.pretups.user.requesthandler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/user/requesthandler/*.java

echo compiling com.btsl.pretups.adjustments.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/adjustments/businesslogic/*.java

echo compiling com.btsl.pretups.channel.query.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/query/businesslogic/*.java

echo compiling com.btsl.pretups.channel.query.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/query/web/*.java

echo compiling com.btsl.pretups.inter.cache
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cache/*.java

echo compiling com.btsl.pretups.inter.connection
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/connection/*.java

echo compiling com.btsl.pretups.inter.ericssion
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/ericssion/*.java

echo compiling com.btsl.pretups.inter.ferma
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/ferma/*.java

echo compiling com.btsl.pretups.inter.module
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/module/*.java

echo compiling com.btsl.pretups.inter.socket
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/socket/*.java

echo compiling com.btsl.pretups.inter.urlcon
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/urlcon/*.java

echo compiling com.btsl.pretups.inter.util
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/util/*.java

echo compiling com.btsl.pretups.routing.subscribermgmt.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/routing/subscribermgmt/web/*.java

echo compiling com.btsl.pretups.routing.subscribermgmt.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/routing/subscribermgmt/businesslogic/*.java

echo compiling com.btsl.pretups.routing.master.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/routing/master/businesslogic/*.java


echo compiling com.btsl.purging
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/purging/*.java


echo compiling com.btsl.pretups.p2p.reconciliation.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/reconciliation/businesslogic/*.java

echo compiling com.btsl.pretups.p2p.reconciliation.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/p2p/reconciliation/web/*.java


echo compileing btsl.pretups.gateway.util
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/gateway/util/*.java

echo compileing btsl.pretups.gateway.parsers
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/gateway/parsers/*.java

echo compileing btsl.pretups.inter.alcatel
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/alcatel/*.java

echo compileing btsl.pretups.inter.alcatel10
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/alcatel10/*.java

echo compileing btsl.pretups.inter.telnet
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/telnet/*.java

echo compiling com.btsl.pretups.channel.reconciliation.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/reconciliation/web/*.java

echo compiling com.btsl.pretups.channel.reconciliation.businesslogic
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/reconciliation/businesslogic/*.java


echo compiling com.btsl.pretups.restrictedsubs.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/restrictedsubs/businesslogic/*.java
echo compiling com.btsl.pretups.restrictedsubs.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/restrictedsubs/web/*.java

echo compiling com.btsl.pretups.scheduletopup.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/scheduletopup/businesslogic/*.java

echo compiling com.btsl.pretups.scheduletopup.process
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/scheduletopup/process/*.java

echo compiling com.btsl.pretups.processes
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/processes/*.java

echo compiling com.btsl.pretups.whitelist.process
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/whitelist/process/*.java

echo compiling com.btsl.pretups.whitelist.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/whitelist/businesslogic/*.java

echo compiling com.btsl.pretups.whitelist.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/whitelist/web/*.java

echo compiling com.btsl.pretups.inter.postqueue
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/postqueue/*.java

echo compiling com.btsl.pretups.inter.alcatel432
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/alcatel432/*.java

echo compiling com.btsl.pretups.inter.post.cdr
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/post/cdr/*.java

echo compiling com.btsl.pretups.inter.siemens
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/siemens/*.java

echo compiling com.btsl.pretups.grouptype.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/grouptype/businesslogic/*.java

echo compiling com.btsl.pretups.batch.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/batch/businesslogic/*.java

echo compiling com.btsl.pretups.inter.cs3
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cs3/*.java

echo compiling com.btsl.pretups.inter.scheduler
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/scheduler/*.java

echo compiling com.btsl.pretups.inter.postonline
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/postonline/*.java

echo compiling com.btsl.pretups.inter.ferma6
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/ferma6/*.java

echo compiling com.btsl.pretups.inter.mobi
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/mobi/*.java

echo compiling com.btsl.pretups.inter.cboss
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cboss/*.java

echo compiling com.btsl.pretups.inter.alcatel442
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/alcatel442/*.java

echo compiling com.btsl.pretups.inter.bank
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/bank/*.java

echo compiling com.btsl.pretups.inter.huaweievr
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/huaweievr/*.java

echo compiling com.btsl.pretups.inter.huawei84
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/huawei84/*.java

echo compiling com.btsl.pretups.inter.bank
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/bank/*.java

echo compiling com.btsl.pretups.inter.alcatel452
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/alcatel452/*.java

echo compiling com.btsl.pretups.inter.cs3cp6
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cs3cp6/*.java

echo compiling com.btsl.pretups.inter.cs3guinea.cs3guineascheduler
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cs3guinea/cs3guineascheduler/*.java

echo compiling com.btsl.pretups.inter.cs3guinea
javac  -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/cs3guinea/*.java

echo compiling com.btsl.pretups.inter.ztekenya
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/inter/ztekenya/*.java

echo compiling com.btsl.vas.businesslogic
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/vas/businesslogic/*.java

echo compiling com.btsl.vas.web
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/vas/web/*.java

echo compiling com.btsl.vas.txt
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/vas/txt/*.java
