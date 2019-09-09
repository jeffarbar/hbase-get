Documentação 



° Configuração

	O serviço é inicializado para cada tabela assim como as configurações do kerberos, para tal, é necessário setar os dados no arquivo application.properties  e gerar um nova imagem através do script load_image.sh

	No arquivo application.properties, também é possível configurar o contexto da aplicação assim como a porta que ira rodar. Isso é importa já que ira montar a URL da api

http://ip:porta/contexto/serviço

Exemplo:

http://localhost:1191/hbase/get



º Utilização

	Com o banco do hbase e o kerberos já definidos, podemos utilizar os serviços da api que consistem em 3 funcionalidades de exposições dos dados do hbase, que são: 

	Primeiro serviço o /noAr, acessado pelo método GET, onde não deve passar nenhum parâmetro, este retorna um json 

Exemplo de como solicitar o método

curl -X GET "http://localhost:1191/hbase/noAr" -H "accept: application/json"

ou

http://localhost:1191/hbase/noAr

Exemplo de retorno

{
	“msg”,”Estou no ar”,
	“codigo”:0
}


	Segundo serviço o /get/{rowkey}, acessado pelo método GET, onde passa como parâmetro a rowkey, este representa a chave da tabela do banco, retornando um json 

Exemplo de como solicitar o método

curl -X GET "http://localhost:1191/hbase/get/5153" -H "accept: application/json"

ou

http://localhost:1191/hbase/get/5153

Exemplo de retorno, retornando dos as famílias e suas respectivas colunas, com os seus valores

{
	"msg":"Sucesso",
	"codigo":0,
	"familias":[
	 	{
			"familia":"produto_tv",
			"colunas":{
				"cod":"41232",
				"nome":"produto de TV"
			}
		},
		{
			"familia":"produto_internet",
			"colunas":{
				"cod":"41232",
				"nome":"produto de TV"
			}
		}
	]
}


	Terceiro serviço o /get/{rowkey}/{familiaColunas}, acessado pelo método GET, onde se passa 2 parâmetros, o primeiro a rowkey, que representa a chave da tabela, e o segundo parâmetro a familiaColunas, esta tem a união da família e coluna, e pode ser enviado mais de um parâmetro. 
	A família e coluna devem ser separado pelo “:” sem aspas, caso não envie os “:” a aplicação ira entender como apenas uma família, e retornará todos os campos dessa família. 
	A separação de varias  familiaColunas é feito pelo “,” sem aspas, não há limite de parâmetros.


Exemplo de como solicitar o método

curl -X GET "http://localhost:1191/hbase/get/5153/produto_internet%3Anome%2Cproduto_tv" -H "accept: application/json"

ou

http://localhost:1191/hbase/get/5153/produto_internet:nome,produto_tv


Exemplo de retorno, retornando dos as famílias e suas respectivas colunas, com os seus valores


{
	"msg":"Sucesso",
	"codigo":0,
	"familias":[
		{
			"familia":"produto_tv",
			"colunas":{
				"cod":"41232",
				"nome":"produto de TV"
			}
		},
		{
			"familia":"produto_internet",
			"colunas":{
				"nome":"produto de TV"
			}
		}
	]
}

	Nota que este serviço retorna todas as colunas de uma família, se a mesma for passada como parâmetro, se retorna um coluna especifica se for passada a família e o coluna

º Valores de Retorno

0, Sucesso;
0, Estou no ar;
91, Ocorreu um erro na sua solicitação
95, Nenhum resultado encontrado, favor validar se o rowid existe na tabela;
99, Falha ao recuperar os campos. Favor validar se os dados família/coluna, na pesquisa estão corretos;

º Swagger

	O swagger esta disponível na URL 

http://localhost:1191/hbase/swagger-ui.html
