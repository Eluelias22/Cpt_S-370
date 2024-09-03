// Assignment 1
// Elias Werede 
// Feb 09, 2024

#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdio.h>

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <string>\n", argv[0]);
        return 1;
    }
    // pipe for connection  
    int pipe0[2];
    int pipe1[2];

    // For creating pipe
    if (pipe(pipe0) == -1 || pipe(pipe1) == -1) {
        perror("pipe creation error");
        return 1;
    }

    // Fork for child
    pid_t pid_ps = fork();
    if (pid_ps == -1) {
        perror("fork error for ps");
        return 1;
    } else if (pid_ps == 0) {
        // close unused pipes
        close(pipe0[0]); 
        close(pipe1[0]); 
        close(pipe1[1]);
        
        if (dup2(pipe0[1], STDOUT_FILENO) == -1) {
            perror("dup2 error for ps");
            _exit(1);
        }
        // close after duplication
        close(pipe0[1]);

        execlp("ps", "ps", "-A", NULL);
        perror("execlp error for ps");
        _exit(1);
    }

    // Fork for the second child
    pid_t pid_grep = fork();
    if (pid_grep == -1) {
        perror("fork error for grep");
        return 1;
    } else if (pid_grep == 0) {
        close(pipe0[1]); // Close write
        close(pipe1[0]); // Close unused read end

        if (dup2(pipe0[0], STDIN_FILENO) == -1) {
            perror("dup2 error for grep");
            _exit(1);
        }
        // close after duplication
        close(pipe0[0]); 

        if (dup2(pipe1[1], STDOUT_FILENO) == -1) {
            perror("dup2 error for grep");
            _exit(1);
        }
        // close after duplication
        close(pipe1[1]); 
        // Execute grep
        execlp("grep", "grep", argv[1], NULL);
        perror("execlp error for grep");
        _exit(1);
    }

    // For parent and close all pipe ends if it is not used
    close(pipe0[0]);
    close(pipe0[1]);
    close(pipe1[1]);

    // wait for both child processes to complete before proceeding
    waitpid(pid_ps, NULL, 0);
    waitpid(pid_grep, NULL, 0);

    // parent now reads the output from grep and counts lines
    int line_count = 0;
    char buffer;
    while (read(pipe1[0], &buffer, 1) > 0) {
        if (buffer == '\n') {
            line_count++;
        }
    }
    // close after reading
    close(pipe1[0]); 
    // display 
    printf("%d\n", line_count);

    return 0;
}
