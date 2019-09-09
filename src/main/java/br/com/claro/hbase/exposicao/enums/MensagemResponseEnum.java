package br.com.claro.hbase.exposicao.enums;

public enum MensagemResponseEnum {
	
	SUCESSO(0,"Sucesso"),
	NENHUM_RESULTADO(95,"Nenhum resultado encontrado, favor validar se o rowid existe na tabela"),
	FALHA_HBASE_RECUPERAR_CAMPOS(99,"Falha ao recuperar os campos. Favor validar se os dados família/coluna, na pesquisa estão corretos"),
	FALHA_HBASE_GET(91,"Ocorreu um erro na sua solicitação"),
	NO_AR(0,"Estou no ar");
	
	private int codigo;
	private String mensagem;
	
	MensagemResponseEnum(int codigo , String mensagem){
		this.codigo = codigo;
		this.mensagem = mensagem;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}	
	
	
