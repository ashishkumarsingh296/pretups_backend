case "$1" in
    start)
        echo " check-----> $1"
        sleep 20
	echo "Starting Apache Server"
        su -c /home/pretups/cluster/httpd2Start.sh
        echo "Started Apache Server"
        ;;
    stop)
        echo " check-----> $1"
        sleep 20
	echo "Stopping Apache Server"
        su -c /home/pretups/cluster/httpd2Stop.sh
        echo "Stopped Apache Server"
    ;;
    status)
        echo " check-----> $1"
        curl 'http://172.24.1.22:8092/index.html'
        if [ $? -eq 7 ] ; then
                echo "Apache Server is not working"
                exit 1;
        fi
        echo "Apache Server is  working"
        exit 0;
        ;;
    *)
    echo "Usage: $0 {start|stop|status}"
    exit 1
esac
exit 0
