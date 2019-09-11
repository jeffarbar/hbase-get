#!/bin/bash

versao_dev=1.0.1
nome_imagem=jeffersonfarias/hbaseget


sudo docker rmi $nome_imagem:$versao_dev

sudo docker build --no-cache -t $nome_imagem:$versao_dev .
sudo docker push $nome_imagem:$versao_dev
	
echo -e "Versão finalizado"


