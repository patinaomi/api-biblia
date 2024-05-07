# Documento de Projeto: VerseBot

## Descrição do Projeto

VerseBot é uma aplicação Java desenvolvida para interagir com usuários através do Telegram, fornecendo funcionalidades relacionadas a versículos bíblicos. A aplicação consome dados de API externas, gerencia a persistência dos dados em um banco de dados e utiliza serviços de inteligência artificial para enriquecer a experiência do usuário.

## Funcionalidades

- **Cadastro de Usuários**: Permite que usuários se cadastrem e mantenham um registro persistente no sistema.
- **Geração de Versículos Aleatórios**: Usuários podem solicitar versículos aleatórios que são buscados através da API da Bíblia Digital.
- **Salvamento de Versículos**: Usuários podem salvar versículos de interesse, que são armazenados no banco de dados.
- **Geração de Devocionais**: Utiliza a API do OpenAI para gerar devocionais baseados em versículos específicos.
- **Síntese de Voz**: Converte textos em áudio usando o serviço IBM Text to Speech.

## Visão Geral


O projeto segue uma arquitetura em camadas, separando claramente a lógica de negócios, acesso a dados e interação com APIs externas.

### Camadas

1. **DAO (Data Access Object)**: Gerencia a comunicação com o banco de dados.
2. **BO (Business Object)**: Contém a lógica de negócios.
3. **API Clients**: Conecta com APIs externas como a Bíblia Digital e OpenAI.
4. **Service**: Orquestra as operações entre a API, BOs e DAOs.
5. **Model**: Define as entidades do sistema.

## Integração com APIs Externas

### API da Bíblia Digital

Usada para buscar versículos aleatórios e informações sobre livros da Bíblia.

### OpenAI

Utilizada para gerar textos devocionais baseados em prompts específicos.

### IBM Watson Text to Speech

Converte textos em áudio para proporcionar uma experiência auditiva dos textos bíblicos.
### API do Telegram 
Essencial para a interação entre o usuário e o VerseBot. Esta API permite que o bot receba mensagens dos usuários e responda com textos, áudios ou comandos interativos. 
#### Funcionalidades Implementadas 
-  **Recebimento de Mensagens**: O bot recebe mensagens de texto dos usuários através do Telegram. 
- **Envio de Respostas**: O bot envia respostas que podem incluir texto, versículos, devocionais e áudios. 
-  **Menus Interativos**: Utiliza botões e comandos interativos para facilitar a navegação do usuário. 
-  **Gerenciamento de Estados**: O bot mantém o estado da conversa para cada usuário, permitindo interações complexas e personalizadas.

## Configuração e Uso

A aplicação utiliza um arquivo `config.properties` para gerenciar configurações sensíveis e variáveis do ambiente.
## Uso de Arquivo `config.properties`

### Localização

O arquivo `config.properties` deve ser colocado no diretório `resources` do projeto. Este diretório é normalmente localizado sob `src/main/resources` em projetos Maven.

### Conteúdo do Arquivo

O arquivo deve conter as chaves e valores para configurações como:

```properties
db.username=SEU_USUARIO
db.password=SUA_SENHA
telegram.api.key=CHAVE_API_TELEGRAM
openai.api.key=CHAVE_API_OPENAI
tts.api.key=CHAVE_API_IBM_TTS
tts.service.url=URL_SERVICO_TTS
```


## Referências e Documentações Consultadas

Durante o desenvolvimento do projeto, várias APIs e bibliotecas foram utilizadas. Abaixo estão os links para a documentação de cada uma, que foram essenciais para o entendimento e aplicação correta das tecnologias.

- **OpenAI Java Client** - Uma biblioteca Java para acessar a GPT-3 da OpenAI. [GitHub Repository](https://github.com/TheoKanning/openai-java)
- **A Bíblia Digital** - Uma API para acessar informações bíblicas. [GitHub Repository](https://github.com/omarciovsena/abibliadigital)
- **Java Telegram Bot API** - Uma biblioteca Java para a criação de bots no Telegram. [GitHub Repository](https://github.com/pengrad/java-telegram-bot-api)
- **IBM Watson Text to Speech** - SDK de Java para o serviço Text to Speech da IBM Watson. [GitHub Repository](https://github.com/watson-developer-cloud/java-sdk/tree/master/text-to-speech)

Estas documentações foram fundamentais para o desenvolvimento das funcionalidades do projeto, permitindo a integração eficiente com os serviços externos utilizados.
