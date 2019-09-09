package br.com.claro.hbase.exposicao.vo;

import br.com.claro.hbase.exposicao.enums.MensagemResponseEnum;

public class ResponseVo {
	
	public ResponseVo(int codigo, String msg) {
		super();
		this.msg = msg;
		this.codigo = codigo;
	}
	
	public ResponseVo(MensagemResponseEnum mensagemResponseEnum) {
		super();
		this.msg = mensagemResponseEnum.getMensagem();
		this.codigo = mensagemResponseEnum.getCodigo();
	}

	private String msg;
	
	private int codigo;


	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
}
