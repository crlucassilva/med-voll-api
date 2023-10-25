# VollMed API
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/crlucassilva/voll.med-api/blob/main/LICENSE)
> Status: Em desenvolvimento

# Sobre o Projeto

É um projeto de uma clínica médica fictícia chamada VollMed. Essa clínica precisa de um aplicativo para monitorar o cadastro de médicos, pacientes e agendamento de consultas.


## Quais são os objetivos

- Desenvolver uma API Rest
- CRUD (Create, Read, Update e Delete)
- Validações
- Paginação e ordenação

## Tecnologias utilizadas

- Java 17
- Spring Boot 3
- JPA / Hibernate
- MySQL / Flyway
- Maven
- Lombok
- Postman

## Desenvolvimento

Para facilitar a descrição das funcionalidades, validações e regras de negócio, foi criado um quadro no Trello, com cada cartão contendo a sua definição.

Para a contrução do projeto foi usado o Spring Initializr.

Em seguida, desenvolvido uma CRUD para medicos e pacientes.

Utilizado o padrão ___DTO (Data Transfer Object)___, via Java Records, para representar os dados recebidos de uma requição. Para validar se as informaçãoes recebidas estão de acordo com as regras de negócio foi utilizado as anotações do Bean Validation.

```java
public record DadosCadastroMedico(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String telefone,

        @NotBlank
        @Pattern(regexp = "\\d{4,6}")
        String crm,

        @NotNull
        Especialidade especialidade,

        @NotNull
        @Valid
        DadosEndereco endereco) {
}
```

Utilizado a biblioteca Lombok nas entidades para reduzir a verbozidade do código, automatizando a geração de getters, setters, contrutores e métodos.

```java
@Entity
@Table(name = "medicos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String crm;
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;
    private Boolean ativo;

    public Medico(DadosCadastroMedico dados) {
       this.nome = dados.nome();
       this.email = dados.email();
       this.crm = dados.crm();
       this.telefone = dados.telefone();
       this.endereco = new Endereco(dados.endereco());
       this.especialidade = dados.especialidade();
       this.ativo = true;
    }

    public void atualizarInformações(DadosAtualizacaoMedico dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.endereco() != null) {
            this.endereco.atualizarInformações(dados.endereco());
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
```

Utilizado o Flyway como ferramenta de Migrations no projeto para controle de histórico da evolição do banco de dados 

![image](https://github.com/crlucassilva/voll.med-api/assets/74364754/e467c09d-5c79-4715-9257-4337897bc8e2)

