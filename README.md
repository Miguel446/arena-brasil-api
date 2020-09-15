# arena-brasil-api
API Restful para agendamento de eventos, nesse caso para arenas de futebol, mas pode ser adaptado facilmente à outros negócios

Projeto desenvolvido para estudar alguns conceitos de Java Spring
- Autenticação no servidor feita via JWT
- Filtro de requisição adicionado para validar token JWT automaticamente em todo o projeto
- Endpoints com níveis de acesso diferentes (ADMIN ou USUARIO)
- DTOs para manipular objetos durante uma requisição e resposta do servidor
- Validação de DTOs como sendo não nulos, de tamanho de string ou de email
- Padronização dessas respostas com o objeto Response que pode conter os dados de retorno ou erros que possam ter ocorrido
- Uso de Optional para evitar Nullpointers
- Uso do Logger para visualizar o uso da API em tempo real e localizar erros mais facilmente
- Uso de Streams, principalmente map, para deixar o código mais limpo
