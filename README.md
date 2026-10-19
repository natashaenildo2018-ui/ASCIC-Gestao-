# ASCIC — Cadastro de Associados e Rondas

Aplicativo Android inicial em Java. Esta entrega contém a base funcional de cadastro local e uma tela informativa para o fluxo de rondas.

## Recursos presentes
- Cadastro de associados com nome, telefone, endereço/comunidade, data e observações.
- Pesquisa por nome ou telefone e exclusão com confirmação.
- Banco de dados SQLite local.
- Tela de rondas com período previsto diariamente das 00h às 06h.
- Programação de notificações locais no aparelho após autorização.
- Explicação dentro do app sobre a integração necessária para acompanhamento online.

## Requisito confirmado para a próxima etapa
A localização da equipe deverá ser visível para todos os clientes cadastrados. Para impedir que pessoas não cadastradas vejam a posição, cada cliente deverá autenticar-se. A equipe deverá usar uma conta de operador com permissão própria para iniciar/finalizar a ronda e compartilhar localização.

## Limitação importante desta versão
**O mapa ao vivo, o envio de GPS para a nuvem e as notificações remotas ainda não estão implementados.** Não há localização fictícia apresentada. O app atual não sincroniza os associados entre aparelhos; o cadastro é local e as notificações são locais.

## O que é necessário para ativar acompanhamento em tempo real
1. Criar um projeto Firebase (Authentication + Realtime Database ou Cloud Firestore + Firebase Cloud Messaging).
2. Configurar login dos clientes e dos operadores de ronda; só clientes cadastrados podem consultar a ronda.
3. Definir regras de segurança no servidor para impedir acesso anônimo e limitar escrita de localização à equipe autorizada.
4. Integrar serviço de localização em primeiro plano no Android, com permissão explícita e notificação persistente enquanto o GPS estiver sendo compartilhado.
5. Enviar atualizações de localização enquanto a ronda estiver ativa, e parar automaticamente ao concluir ou às 06h.
6. Exibir o mapa e o status para clientes autenticados e enviar notificações remotas de início, atualizações relevantes e conclusão.
7. Testar permissões, consumo de bateria, conectividade, segurança e política de privacidade antes de uso real.

Não coloque chaves privadas ou credenciais de serviço dentro do APK. Regras de acesso devem ser aplicadas no servidor, não apenas escondendo botões no aplicativo. A localização da equipe só deve ser compartilhada durante o turno e com consentimento e aviso claros.

## Gerar APK pelo GitHub Actions
1. Crie um repositório GitHub chamado `ASCIC`.
2. Envie o conteúdo desta pasta para a raiz do repositório, incluindo `.github/workflows/build-apk.yml`.
3. Abra a aba **Actions** e execute **Build ASCIC APK**, ou faça push para a branch `main`.
4. Baixe o artefato `ASCIC-app-debug-apk` e extraia `app-debug.apk`.
5. Transfira o APK para o Android e autorize a instalação, se solicitado.

## Compilar localmente
É necessário JDK 17, Android SDK (API 35) e Gradle 8.9. Na raiz do projeto, execute:

```bash
gradle assembleDebug
```

O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Privacidade
Esta versão guarda os cadastros apenas no aparelho e não tem login, sincronização ou cópia de segurança automática. Evite inserir informações sensíveis até definir controles de acesso, consentimento, retenção e backup.
