/* Sample TCP server */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <byteswap.h>

#define BUFLEN 1000000

int sock_fd, cli_fd;

int initializeConnection(int port){
    struct sockaddr_in serv_addr;
    int ret;
    int sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (!sock_fd) {
		perror("socket");
		exit(EXIT_FAILURE);
	}

	bzero((char*)&serv_addr, sizeof(serv_addr));
	// fill in the socket family, address and port

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_addr.s_addr=htonl(INADDR_ANY);
	serv_addr.sin_port=htons(port);

	// set SO_REUSEADDR socket option (please explain the option's meaning)
	/*From man socket:
        Indicates that the rules used in validating addresses supplied
              in a bind(2) call should allow reuse of local addresses.  For
              AF_INET sockets this means that a socket may bind, except when
              there is an active listening socket bound to the address.
              When the listening socket is bound to INADDR_ANY with a
              specific port then it is not possible to bind to this port for
              any local address.  Argument is an integer boolean flag.
	*/
	int so_reuseaddr = 1;
	ret = setsockopt(sock_fd,SOL_SOCKET,SO_REUSEADDR,&so_reuseaddr, sizeof so_reuseaddr);
	if (ret<0) {
		perror("setsockopt");
	}
	// bind with the use of bind procedure
	ret = bind(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
	if (ret<0) {
		perror("bind");
	}
	// start listening with the use of listen procedure
	listen(sock_fd, 5);
	return sock_fd;
}

int getFilenameSize(char recvline[]){
    char buffer[4];
    for(int i=0; i<4; i++){
        buffer[i]=recvline[i];
    }
    uint32_t num = *(uint32_t *)&buffer;
    return __bswap_32(num);
}

static void catch_function(int signo){
    printf("Caught signal, shutting down\n");
    close(cli_fd);
    close(sock_fd);
    exit(-1);
}

int main(int argc, char **argv) {
    if(signal(SIGINT, catch_function) == SIG_ERR){
        printf("An error occurred while setting a signal handler\n");
        return EXIT_FAILURE;
    }
	int len;
	int cli_len;
	struct sockaddr_in cli_addr;
	char recvline[BUFLEN];

	if (argc != 2) {
		printf("usage: %s <TCP port>\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	// create the socket (add missing arguments)
    sock_fd=initializeConnection(atoi(argv[1]));

	while (1) {
		// accept the connection and assign descriptor to cli_fd
		cli_fd=accept(sock_fd, (struct sockaddr*)&cli_addr, &cli_len);

		// receive data to recvline buffer with the "recv" system call and assign number of received bytes to len
		len=recv(cli_fd, recvline, BUFLEN, 0);
		printf("received bytes: %d\n", len);
		recvline[len] = 0;



		close(cli_fd);
	}

	return EXIT_SUCCESS;
}

