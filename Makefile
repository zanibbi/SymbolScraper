default:
	mvn install
	cd bin; ./genbin

clean:
	mvn clean
	rm bin/sscraper


