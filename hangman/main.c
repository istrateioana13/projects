#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Function to form masked word

char *formare_cuvant(char *cuv)
{
    int len = strlen(cuv);
    char *aux = malloc(len + 1); 

    if (!aux) {
        perror("malloc failed");
        exit(1);
    }

    aux[0] = cuv[0];  

    for (int i = 1; i < len - 1; i++) {
        if (cuv[i] == cuv[0]) {
            aux[i] = cuv[0];
        } else if (cuv[i] == cuv[len - 2]) {
            aux[i] = cuv[i];
        } else {
            aux[i] = '_';
        }
    }

    aux[len - 1] = '\0';
    return aux;
}


// Check if guessed letter exists in word

int cautare_litera(char* cuv, char* aux, char lit)
{
    int gasit = 0;
    for (int i = 0; cuv[i] != '\0'; i++) {
        if (lit == cuv[i] && aux[i] == '_') {
            aux[i] = lit;
            gasit = 1;
        }
    }
    return gasit;
}

// Create a new word (without newline at the end)

char *cuv_nou(char *cuv)
{
    int len = strlen(cuv);
    char *aux = malloc(len);

    if (!aux) {
        perror("malloc failed");
        exit(1);
    }

    strcpy(aux, cuv);
    if (aux[len - 1] == '\n') {
        aux[len - 1] = '\0'; 
    }
    return aux;
}


// Main game loop

int main() {
    char cuv[50], *aux, lit, *cuvant;
    char nume[50] = "";
    int lit_gresite, c = 0, choice, back, gasit;

    FILE* fp = fopen("cuvinte.txt", "r");
    if (!fp) {
        perror("Nu s-a putut deschide fisierul cuvinte.txt");
        return 1;
    }

    while (1) {
        system("clear"); 
        printf("~SPANZURATOAREA~\n");
        printf("1. Start\n");
        printf("2. Clasament\n");
        printf("3. Exit\n");
        printf("Introduceti varianta: ");
        if (scanf("%d", &choice) != 1) break;

        if (choice == 1) {
            rewind(fp); 

            while (fgets(cuv, sizeof(cuv), fp)) {
                lit_gresite = 0;
                aux = formare_cuvant(cuv);

                while (1) {
                    system("clear");
                    printf("%s\n", aux);
                    printf("Litere gresite: %d\n", lit_gresite);
                    printf("Scor: %d\n", c);
                    printf("Introduceti litera: ");
                    scanf(" %c", &lit); 

                    gasit = cautare_litera(cuv, aux, lit);
                    cuvant = cuv_nou(cuv);

                    if (!gasit) {
                        lit_gresite++;
                    } else if (strcmp(cuvant, aux) == 0) {
                        c += strlen(aux);
                        free(cuvant);
                        break;
                    }
                    free(cuvant);

                    if (lit_gresite == 5) break;
                }

                if (lit_gresite == 5) {
                    system("clear");
                    printf("Ati gresit de %d ori. Jocul s-a terminat\n", lit_gresite);
                    printf("Scor: %d\n", c);
                    printf("Introduceti numele: ");
                    scanf("%49s", nume); 
                    free(aux);
                    break;
                }

                free(aux);
            }
        }

        else if (choice == 2) {
            system("clear");
            printf("-=CLASAMENT=-\n");
            printf("NUME SCOR\n");
            if (strlen(nume) > 0) {
                printf("%s %d\n", nume, c);
            } else {
                printf("Nimeni inca.\n");
            }
            printf("1. Back\n");
            scanf("%d", &back);
        }

        else if (choice == 3) {
            break;
        }
    }

    fclose(fp);
    return 0;
}
