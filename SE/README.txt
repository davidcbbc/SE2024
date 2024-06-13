ReadMe SE2024


Instruções de Configuração e Utilização:

1) Ligar os Componentes:

Ligar o Raspberry Pi: Certifique-se de que o Raspberry Pi está ligado e a funcionar corretamente.

Ligar a Aplicação no Telemóvel: Abra a aplicação no seu telemóvel.

Ligar o Arduino: Conecte e ligue o Arduino.

Conectar à mesma Rede de Internet: Certifique-se de que todos os componentes (Raspberry Pi, aplicação no telemóvel e Arduino) estão conectados à mesma rede de internet. No nosso caso, conectamo-nos ao ponto de acesso móvel de um telemóvel.

2) Iniciar o Servidor no Raspberry Pi:

Abrir o Terminal: No Raspberry Pi, abra o terminal.
Execute o seguinte comando para iniciar o servidor e estabelecer a comunicação entre os componentes:

$ python3 initiate_all.py

Este comando inicia o servidor para a troca de mensagens entre o Raspberry Pi e a aplicação móvel, bem como a comunicação entre o Arduino e o Raspberry Pi. Também inicia o videostream da roleta e abre também a base de dados para ver os seus detalhes.

3) Testar a Configuração:

Criar uma Conta na Aplicação: Abra a aplicação no telemóvel e crie uma conta.

Iniciar Sessão: Faça login na conta criada.

Adicionar Saldo: Clicar em "account" -> "add balance" para adicionar saldo à sua conta.

Fazer uma Aposta: Clique no botão "Betting", escolha um número da roleta e insira o montante desejado para apostar.

Ver a Roleta a Girar: Para ver a roleta a girar, clique no botão "Livestream". Ai poderá ver a roleta a funcionar em tempo real.
No fim da ronda, o dinheiro ganho será adicionado automaticamente à conta respetiva.

4) Resolução de Possíveis Problemas

Conexão de Rede:
Verifique se todos os dispositivos estão na mesma rede.
Reinicie o ponto de acesso móvel e tente novamente.
Verifique a configuração dos parâmetros de rede definidos no código de cada componente (Raspberry Pi, Arduino e aplicação móvel).

Considerações Finais: Certifique-se de que todos os passos acima foram seguidos corretamente para garantir que o sistema funcione sem problemas. Em caso de problemas, verifique as conexões de rede e a execução correta do comando no Raspberry Pi.
