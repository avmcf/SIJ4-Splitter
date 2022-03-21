package com.vconsulte.sij.splitter;

public class IndiceEdicao {
 	
	public String secao;
	public int paginaSecao;
	public int linhaSecao;
	public String complementoSecao;
	public String grupo;
	public int paginaGrupo;
	public int linhaGrupo;
	public int indexSecao;
	public int indexGrupo;
		
	public IndiceEdicao(String secao, 
			int paginaSecao, 
			int linhaSecao, 
			String complementoSecao, 
			String grupo, 
			int paginaGrupo,  
			int linhaGrupo, 
			int indexSecao,
			int indexGrupo) {
		
		this.secao = secao;
		this.paginaSecao = paginaSecao;
		this.linhaSecao = linhaSecao;
		this.complementoSecao = complementoSecao;
		this.grupo = grupo;
		this.paginaGrupo = paginaGrupo;
		this.linhaGrupo = linhaGrupo;
		this.indexGrupo = indexGrupo;
		this.indexSecao = indexSecao;
	}

	public String getSecao() { 
		return secao; 
	}
	public void setSecao(String secao) { 
		this.secao = secao; 
	}
	// ------------------------------------
	
	public int getPaginaSecao() { 
		return paginaSecao; 
	}
	public void setPaginaSecao(int paginaSecao) { 
		this.paginaSecao = paginaSecao; 
	}
	// ------------------------------------
		
	public int getLinhaSecao() { 
		return linhaSecao; 
	}
	public void setLinhaSecao(int linhaSecao) { 
		this.linhaSecao = linhaSecao; 
	}
	// ------------------------------------
	
	public String getcomplementoSecao() { 
		return complementoSecao; 
	}
	public void setcomplementoaSessao(String complementoSecao) { 
		this.complementoSecao = complementoSecao; 
	}
	// ------------------------------------
	
	public String getGrupo() { 
		return grupo; 
	}
	public void setGrupo(String grupo) { 
		this.grupo = grupo; 
	}
	// ------------------------------------
	
	public int getPaginaGrupo() { 
		return paginaGrupo; 
	}
	public void setPaginaGrupo(int paginaGrupo) { 
		this.paginaGrupo = paginaGrupo; 
	}
	
	// ------------------------------------
	
	public int getLinhaGrupo() { 
		return linhaGrupo; 
	}
	public void setLinhaGrupo(int linhaGrupo) { 
		this.linhaGrupo = linhaGrupo; 
	}
	// ------------------------------------
	
	public int getIndexGrupo() { 
		return indexGrupo; 
	}
	public void setIndexGrupo(int indexGrupo) { 
		this.indexGrupo = indexGrupo; 
	}
	// ------------------------------------
	
	public int getindexSecao() { 
		return indexSecao; 
	}
	public void setIndexSecao(int indexSecao) { 
		this.indexSecao = indexSecao; 
	}
	// ------------------------------------
		
}

