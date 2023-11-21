# VollMed API
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/crlucassilva/voll.med-api/blob/main/LICENSE)
> Status: Finalizado!

# Sobre o Projeto

É um projeto de uma clínica médica fictícia chamada VollMed. Essa clínica precisa de um aplicativo para monitorar o cadastro de médicos, pacientes e agendamento de consultas.


## Quais são os objetivos

- Desenvolver uma API Rest
- CRUD (Create, Read, Update e Delete)
- Validações
- Paginação e ordenação
- Boas práticas na API
- Tratamento de erros
- Autenticação/Autorização
- Tokens JWT
- Regras de Negócio
- Documentar API
- Testar API
- Build do Projeto

## Tecnologias utilizadas

- Java 17
- Spring Boot 3
- Spring Security
- Spring Doc
- JPA / Hibernate
- MySQL / Flyway
- Maven
- Lombok
- Postman
- JSON Web Token (JWT)

## Desenvolvimento

Para facilitar a descrição das funcionalidades, validações e regras de negócio, foi criado um quadro no Trello, com cada cartão contendo a sua definição.

Para a contrução do projeto foi usado o Spring Initializr.

Em seguida, desenvolvido uma CRUD para medicos e pacientes.

Utilizado o padrão ___DTO (Data Transfer Object)___, via Java Records, para representar os dados recebidos de uma requição. Para validar se as informaçãoes recebidas estão de acordo com as regras de negócio foi utilizado as anotações do Bean Validation.

```java
public record DadosCadastroMedico(

       @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato do email é inválido")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "CRM é obrigatório")
        @Pattern(regexp = "\\d{4,6}")
        String crm,

        @NotNull(message = "Especialidade é obrigatória")
        Especialidade especialidade,

        @NotNull(message = "Dados do endereço são obrigatórios")
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

![image](https://github.com/crlucassilva/voll.med-api/assets/74364754/b90a2bae-e81b-4e07-86d6-559673ed8ba5)

Foi adicionado e configurado o Spring Security e JWT para autenticação e autorização de usuários

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/medicos/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/pacientes/*").hasRole("ADMIN")
                    .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

```java
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager maneger;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = maneger.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}
```

```java
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var usuario = repository.findByLogin(subject);

            var authenticarion = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticarion);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
}
```

Utilizado o Junit para realizar os testes necessários da aplicação. 

E por fim, o build do projeto.








