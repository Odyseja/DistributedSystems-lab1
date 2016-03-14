/* Sample TCP client */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <byteswap.h>
#include <strings.h>
#include <unistd.h>

#define BUFLEN 1

struct sockaddr_in serv_addr;
int len;
int sock_fd;
char port[100];
char address[100];

int connectToServer(){
    sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (!sock_fd) {
		perror("socket");
		exit(EXIT_FAILURE);
	}
	bzero((char*)&serv_addr, sizeof(serv_addr));
    // fill in the socket family, address and port
	serv_addr.sin_family=AF_INET;
	serv_addr.sin_addr.s_addr=inet_addr(address);
	serv_addr.sin_port=htons(atoi(port));
	connect(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
	return sock_fd;
}

char* sendLong(int64_t num){
    char arr[8];
    char recvline[1];
    int sock_fd = connectToServer();

    arr[0] = (num >> 56) & 0xFF;
    arr[1] = (num >> 48) & 0xFF;
    arr[2] = (num >> 40) & 0xFF;
    arr[3] = (num >> 32) & 0xFF;
    arr[4] = (num >> 24) & 0xFF;
    arr[5] = (num >> 16) & 0xFF;
    arr[6] = (num >> 8) & 0xFF;
    arr[7] = num & 0xFF;

    len = send(sock_fd, arr, 8, 0);
    printf("%d with size %d sent. Waiting for response\n", num, len);
    len=recv(sock_fd, recvline, BUFLEN, 0);
	printf("received: %d\n", (int)recvline[0]);
    close(sock_fd);
}

void sendInt(int32_t num){
    char arr[5];
    char recvline[1];
    int sock_fd = connectToServer();

    arr[0] = (num >> 24) & 0xFF;
    arr[1] = (num >> 16) & 0xFF;
    arr[2] = (num >> 8 ) & 0xFF;
    arr[3] = num & 0xFF;

    len=send(sock_fd, arr, 4, 0);
    printf("%d with size %d sent. Waiting for response\n", num, len);
    len=recv(sock_fd, recvline, BUFLEN, 0);
	printf("received: %d\n", (int)recvline[0]);
    close(sock_fd);
}

void sendShortInt(int16_t num){
    char arr[2];
    char recvline[1];
    int sock_fd = connectToServer();

    arr[0] = (num >> 8) & 0xFF;
    arr[1] = num & 0xFF;

    len=send(sock_fd, arr, 2, 0);
    printf("%d with size %d sent. Waiting for response\n", num, len);
    len=recv(sock_fd, recvline, BUFLEN, 0);
	printf("received: %d\n", (int)recvline[0], len);
    close(sock_fd);
}

void sendChar(int8_t num){
    char arr[1];
    char recvline[1];
    int sock_fd = connectToServer();
    arr[0] = num & 0xFF;

    len = send(sock_fd, arr, 1, 0);
    printf("%d with size %d sent. Waiting for response\n", num, len);
    len=recv(sock_fd, recvline, BUFLEN, 0);
    printf("Received size: %d\n", len);
	printf("received: %d\n", (int)recvline[0]);
    close(sock_fd);
}


int main(int argc, char **argv) {
	if (argc != 3) {
		printf("usage: %s <IP address> <TCP port>\n", argv[0]);
		exit(EXIT_FAILURE);
	}
	strcpy(address, argv[1]);
	strcpy(port, argv[2]);

    int8_t byte = 24;
    int16_t bytes2 = 30005;
    int32_t bytes4 = 2000000003;
    int64_t bytes8 = 1500001;

    sendChar(byte);
    sendShortInt(bytes2);
    sendInt(bytes4);
    sendLong(bytes8);


	return EXIT_SUCCESS;
}

