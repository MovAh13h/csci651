docker build --rm -t javaapptest . 
docker rmi $(docker images -qa -f 'dangling=true')