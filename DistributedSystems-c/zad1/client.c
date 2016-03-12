/* Sample TCP client */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>

#define BUFLEN 10000

int main(int argc, char **argv) {
	int sock_fd;
	int len;
	struct sockaddr_in serv_addr;
	char sendline[] = "PING";
	char recvline[BUFLEN];

	if (argc != 3) {
		printf("usage: %s <IP address> <TCP port>\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	// create the socket (add missing arguments)
	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (!sock_fd) {
		perror("socket");
		exit(EXIT_FAILURE);
	}

	bzero((char*)&serv_addr, sizeof(serv_addr));
	// fill in the socket family, address and port
	serv_addr.sin_family=AF_INET;
	serv_addr.sin_addr.s_addr=inet_addr(argv[1]);
	serv_addr.sin_port=htons(atoi(argv[2]));

	// establish the connection (SYN, SYN+ANK, ACK) with "connect" procedure
	connect(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));

	// send sendline buffer with the "send" system call and assign number of sent bytes to len
	len=send(sock_fd, sendline, strlen(sendline), 0);
	printf("sent bytes: %d\n", len);
	printf("sent: %s\n", sendline);

	// receive data to recvline buffer with the "recv" system call and assign number of received bytes to len
	len=recv(sock_fd, recvline, BUFLEN, 0);
	printf("received bytes: %d\n", len);
	recvline[len] = 0;
	printf("received: %s\n", recvline);

	return EXIT_SUCCESS;
}

