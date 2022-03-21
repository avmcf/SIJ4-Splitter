package com.vconsulte.sij.splitter;

/*
 * SplitDOb - Versão batch 
 */

//***************************************************************************************************
// SgjProcDO: Antiga rotina clippingDO atualizada para gravar os editais diretamente no Alfresco 	
// 
// Versão 1.7 	- 23 Março de 2017
//					Correção de falha na finalização geral da rotina
//					Correção na possibilidade de encerramento prematuro da rotina
//					Inclusão de lógica para descartar linhas desnecessárias
//					Quebra de pauta de julgamento
//					Processamento de acórdãos
//
// Versao 1.7.1 	- 29 Março de 2017
//					Vericar exceção ao tipo de quebra de editais (verificaExcecaoQuebra)
//					Limpeza do buffer de introdução quando mudar o assnto
//					Generalização do método validaPadrao e verificaAssunto (atender qualquer região)
//					Nova lógia para validação do "assunto", impedir que uma palavra igual a assunto engane a lógica
//
// Versão 1.8 		- 1º de Abril de 2017
//					Nova logica de controle de leitura de linhas e quebra de editais
//					Testado com os DO do RJ e SP
//
// Versão 1.8.1		- 29 de Abril de 2017
// 					Correção na carga de sessões
//
// Versão 1.8.2 	- 08 de Junho de 2017
//					Inclusão de mais dois padrões de nº de processo
//					Correção para gravação do último edital
//
// versao 1.8.3 	- 01 de Julho de 2017
//					Adcição do tribunal e  nº do edital ao nome do arquivo saida.
//
// versao 1.8.3.7 	- 10 de Julho de 2017
// 					conpilado com versão do Java = 7 para manter compatibilidade com sistemas do PJE.
//
// versao 1.9 		- 17 de Setembro de 2017
//					criação da lista "grupos".
//					nova nomeação do arquivos saida.
//					apagar arquivo intermediario.txt.
//					forçar quebra quando o assunto for "Edital de Notificacao"
//					correção na logica de quebra por nova secao - limpar grupoAnterior e assuntoAnterior
// 					melhorias na logica de separação dos grupos
//					melhorias na logina de quebra por grupo e assunto
//     				inclusão de mais um padrão de nº de processo: "\\w{8}\\s\\w\\W\\s\\w{6}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}"
//
// versao 1.9.1 	- 06 de Outubro de 2017
//					Pequenas correções
//					Alteração nas leituras do intermedio com unicode UTF-8
//
// versao 1.9.2 	- 30 de Outubro de 2017
//					Gravação do arquivo de saida em UTF-8
//					Correção na quebra por nº de processo
//
// versao 1.9.3 	- 28 de Fevereiro de 2018
//					Inclusão de mais dois padrões de numeração de processos
//					
//	versao 1.9.4 	- 25 de Maio de 2018
//					Evitar carregar a string "PODER JUDICIARIO" na tabela de assuntos
//
//	versao 1.9.5 	- 31 de Maio de 2018
//					Correção de loop infinito na função mapeiaLinhas
//
//	versao 1.9.6 	- 08 de Junho de 2018
//					Correção na funcão "mapeiaLinhas"
//
// ---------------------------------------------------------------------------------------------------------------------------
//	Nova versão da antiga classe clippingDO agora chamada de SplitDO
//
//	versao 2.0 		- .. de Junho de 2018
//					Classe renomeada para SplitDO.java
//					Integração CMIS
//					Gravação dos editais diretamente no servidor Alfresco
//
//	versao 2.1 		- 17 de Dezembro de 2018
//					Inclusão do tratamento do DO do Tribunal Superior do Trabalho - TST
//
//	versao 2.1.1 	- 27 de Dezembro de 2018
//					Correção do método obtemNumeroProcesso
//
//	versao 2.1.2 	- 28 de Janeiro de 2019
//					Correção do método validaPadrao incluindo mais um formato de nº de processo
//
//	versao 2.1.3 	- 06 de Fevereiro de 2019
//					Incluindo mais um formato de nº de processo
//					Correção do metodp carregaAssuntos para excluir a string "PODER JUDICIÀRIO"
//
//	versao 2.2 		- 26 de Fevereiro de 2019
//					Implementação de novo modelo de dados
//					reformulação do metodo obtemNumeroProcesso
//					reformulação do metodo mapeiaLinha para não utilizar o metodo validaPadrao
//
//	versao 2.2.3 	- 27 de Fevereiro de 2019
//					Correção na quebra de editais por nº de processo
//
//	versao 2.2.4 	- 13 de Março de 2019
//					Correção no metodo "mapearLinhas" para tratar qdo o grupo for igual a "Portaria"
//
//	versao 2.2.5 	- 24 de Março de 2019
//					Correção de erros da versão 2.2.4
//
//	versao 2.3.0 	- 16 de Abril de 2019
//					- Nova logica para determinar quebra por assunto
//					- inclusão do método validaAssunto
//					- inclusão do método contarPalavrasAssunto
//
//	versao 2.3.20 	- 27 de Abril de 2020
//					- Novo algorítimo de quebra de editais
//					
//	
//	versao 2.3.21 	- 04 de Maio 2020
//					- Correções na justificação do texto
//					- Diário oficial convertido para a memoria
//					- Novo método carregaIndice
//					
//	versao 2.3.21a	- 10 de Maio 2020
//					- Ajustes no método formataaParagrafo
//					- Atualização na tabela termosChaves
//					- Atualização na tabela falsoFinal
//
//
//	versao 2.3.21b	- 06 de Junho 2020
//					- Parametrização 
//					- Classe de MetodosComuns.java
//					- Correção na quebra das publicações com assunto Pauta
//									
//	versao 2.3.21c	- 17 de Junho 2020			
//					- Correção na quebra das publicações com assunto Pauta
//					- Ajustes no método trataIntimados
//					
//	versao 2.3.21d	- 18 de Junho 2020		
//					- versão provivisoria sem o controle de nº de página
//
//	versao 2.3.21e	- 04 de Julho 2020		
//					- Parametrização para pastaSaida onde serão gravados as publicações PDF
//					- Melhorias no modelo de dados
//					- Correções na quebra por nº de processo
//					- Correções no método trataAtores
// 					- Melhorias no método trataIntimados
//
//	versao 2.3.21f	- 14 de Julho 2020	
//					- Correções na quebra por nº de processo
//
//	versao 2.3.23	- 01 de Outubro 2020	
//					- Correções na quebra quando a publicação for "Pauta"
//
//	versao 2.4		- 01 de Julho 2020	
//					- Unificação do modelo de dados
//					- Ajustes na saida de publicações em PDF
//					- Novo mentodo apresentaMensagem para apresentação de mensagens conforme o tipo de processamento
//
//	versao 2.4.06	- 06 de Novembro 2020
//					- Atualização na tabela de funções
//					- Atualização do método gravaLog();
//					- Adição do método gravaListaDePublicacoes()
//					- Adição de linha de rodapé das publicacoes com informações sobre a publicação
//
//	versao 2.4.07	- 11 de Novembro 2020
//					- Correção na falha que duplicava publicações retornando o sequencial no fim do loop de linhas.
//					- Melhorias no ClippingB e no BatchDO
//					- Classe Transmite com temporizador
//	
//	versao 2.4.08	- 25 de Novembro 2020
//					- Melhorias no registro de logs
//					- Melhorias no loop de processamento
//					- Tratamento de edições sem publicações (vazia)
//					- Batch.jar: Controle de edições para processamento
//					- Transmite.jar: Parametrização 
//
//	versao 2.4.09	- 04 de Dezembro 2020
//					- Processamento recebendo parametros com os TRTs a serem processados
//					- Controle do ciclo de processament através de rotina bash
//					- Retorno ao processamento por rotina (1-splitDO.jar, 2-Transmit.sh, Clippingb.jar)
//					- Atualização da regra de quebra por nº de processo para ajustar as publicações tipo PAUTA
//
//	versao 2.4.10	- 14 de Dezembro 2020
//					- Correções de junções de publicações
//					- Otimização dos loops de processamento
//					- Nova versão do Clippingb.java permitindo parametrização
//
//	versao 2.5.1	- 21 de janeiro 2021
//					- Atribuição da propriedade processo com apenas o nº do processo da publicação
//					- Pesquisa avançada sobre publicações do repositório
//					- Correção no método "mapeiaLinhas"
//
//	versao 2.5.2	- 24 de março 2021
//					- Atualizações nos algrítimos de quebra de publicações
//					- Atualização na leitura de linhas para evitar inconsistencias com o nº do processo
//
//	versao 2.5.2a	- 25 de março 2021
//					- Correções nas regras de quebra implementadas na versao 2.5.2
//
//	versao 2.5.3a	- 29 de março 2021
//					- Atualização na regra de quebra de processo 
//					- SplitDO: Atualização na regra de tratamento de atores, para prever fim dos atores sem a marca "intimados"
//
//	versao 2.6		- .. de março 2021
//					- Correções na versão 2.5.3a
//					- Correções na finalização das publicações
//					- Inclusão do resumo com quantidade de publicações processadas
//
// 	versao 2.6.1	- 10 de abril 2021
//					- correções no método carregarIndice
//					- inclusão do método posicionarIndice
// 					- utilização do método Comuns.apresentamensagem
//
//	versao 2.7		- 16 de abril 2021
//					- Inclusão de méteodo formataBufferEntrada() para formatar o bufferEntrada 
//
//
//	versao 2.7.1	- 20 de abril 2021
//					- Correções no loop de leituras zerando os buffers de entrada e formatado
//
//	versao 2.7.2	- 25 de abril 2021
//					- Tratamento no método mapeiaLinha() para tratar duplicidades no indice do diario oficial
//
//
//	versao 2.8		- 04 de maio 2021
//					- Atualizaações nos algorítimos de quebra
//
//
//	versao 2.9		- 27 de Junho 2021
//					- Loop de leitura de edições idenpendente de parametros informando quais TRT serão processados
//					- Envio do relatorio para o servidor
//
//
//	versao 2.A		- 09 de Outubro 2021
//					- Gravar tabela de quantidades de publicacoes geradas para verificação pela classe ClipDO
//					- Ler edicoes para processar a partir de pasta local
//
//
//
//
// 	V&C Consultoria Ltda.
// 	Autor: Arlindo Viana.
//***************************************************************************************************

	import java.io.BufferedReader;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileFilter;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
//import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
	import java.text.SimpleDateFormat;

	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Collection;
	import java.util.Date;
	import java.util.List;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	
	import java.nio.charset.StandardCharsets;
	import java.nio.file.*;

	import org.apache.chemistry.opencmis.client.api.Folder;
	import org.apache.chemistry.opencmis.client.api.Session;
	//import org.apache.commons.logging.Log;						Verificar necessidade
	//import org.apache.commons.logging.LogFactory;					Verificar necessidade
	import org.apache.pdfbox.pdmodel.PDDocument;
	import org.apache.pdfbox.text.PDFTextStripper;
	
	import com.vconsulte.sij.base.Base;
	import com.vconsulte.sij.base.Configuracao;
	import com.vconsulte.sij.base.Edital;
	import com.vconsulte.sij.base.GravaTexto;
	import com.vconsulte.sij.base.InterfaceServidor;
	import com.vconsulte.sij.base.MsgWindow;
	import com.vconsulte.sij.base.SalvaPdf;
import com.vconsulte.sij.base.GravaXml;
	import com.vconsulte.sij.base.SelecionaDO;

import lixo.SijBulkImport;

import com.vconsulte.sij.base.Comuns;
	import com.vconsulte.sij.base.BaixaConteudo;
	
public class SplitDO  {

	static File diarioInput;
	static File editaisDir;
	static File assuntos;
	static File config;
	static File diretorio;
	static File intermedio;
	static File edicaoEscolhida;
	
	static Path pathEdicao;
	static Date DtEdicao;
	
	static Folder editalFolder;
	
	static FileWriter arquivoLog;
	static FileWriter arqSaida;
	static FileWriter fileW;
	static BufferedWriter buffW;

	// parametros de configuração
	static String usuario;						// usuario alfresco
	static String password;						// password alfresco
	static String pastaCarregamento;			// Pasta alfresco que recebe as publicacoes
	static String tipoDocumento;				// Tipo do documento no Alfresco
	static String versaoSplitter;				// Versão do SIJ
	static String pastaSaida;					// pasta onde são gravados as publicacoes
	static String tipoProcessamento;			// Tipo do processamento DESKTOP ou Batch
	static String pastaEdicoes;					// De onde ler as edicoes.pdf
	static String tipoArquivoSaida;				// tipo arquivo saida PDF ou Texto
	static String pastaDeEdicoes;				// tipo arquivo saida PDF ou Texto
	static String pastaIndiceXML;				// Pasta onde será grava do o Indice.XML da edicao
	static String pastaRelatorios; 				// Pasta onde serão gravados os relatorios
	
	static List<com.vconsulte.sij.splitter.IndiceEdicao> Index = new ArrayList<com.vconsulte.sij.splitter.IndiceEdicao>();
	static com.vconsulte.sij.splitter.IndiceEdicao Indice = new com.vconsulte.sij.splitter.IndiceEdicao(null, 0, 0, null, null, 0,  0, 0, 0);
	
	static List <String> tabelaAssuntos = new ArrayList<String>();
	static List <String> log = new ArrayList<String>();
	static List <String> listaDePublicacoes = new ArrayList<String>();
	static List <String> resumoPublicacoes = new ArrayList<String>();
	static List <String> totalPublicacoesTribunal = new ArrayList<String>();
	static List <String> assuntosUtilizados = new ArrayList<String>();
	static List <String> continuacoesPossiveis = new ArrayList<String>();
	static List <String> bufferEntrada = new ArrayList<String>();
	static List <String> bufferFormatado = new ArrayList<String>();
	static List <String> tabelaAtores = com.vconsulte.sij.base.Parametros.TABELAUTORES;
	static List <String> juridiques = com.vconsulte.sij.base.Parametros.JURIDIQUES;
	static List <String> falsoFinal = com.vconsulte.sij.base.Parametros.FALSOFINAL;
	static List <String> stopWords = com.vconsulte.sij.base.Parametros.STOPWORDS;
	static List <String> keyWords = com.vconsulte.sij.base.Parametros.KEYWORDS;
	static List <String> funcoes = com.vconsulte.sij.base.Parametros.FUNCOES;
	static List <String> continuadores = com.vconsulte.sij.base.Parametros.CONTINUADORES;
	static List <String> meses = com.vconsulte.sij.base.Parametros.MESES;
	
	static Collection<String> bufferSaida = new ArrayList<String>();			// estar sem uso?
	
	static ArrayList<String> edicaoXML = new ArrayList<String>();
	static ArrayList<String> textoEdital = new ArrayList<String>();
	static ArrayList<String> textoSaida = new ArrayList<String>();
	static ArrayList<String> textoIntroducao = new ArrayList<String>();
	static ArrayList<String> edital = new ArrayList<String>();
	static ArrayList<String> introducao = new ArrayList<String>();
	static ArrayList<String> paragrafos = new ArrayList<String>();
	static ArrayList<String> padraoGrupo = new ArrayList<String>();
	static ArrayList<String> edicoesProcessadas = new ArrayList<String>();
	
	static String [] parametros = null;
	static String [][] tabEdicoes = new String[30][2];

    static String editalTexto = "";
	static String textoTeste = "";	
	static String tribunal = "";
	static String strTribunal = ""; 
	static String titulo1 = "";
	static String titulo2 = "";
	static String titulo3 = "";
	static String titulo4 = "";
	static String titulo5 = "";
	static String rodape = "";
	static String dataEdicao = "";
	static String complementoDescricao = "";
	static String descricaoFolder = "";
	static String seqEdicao;
	static String strEdicao;
	static String secao = "";
	static String grupo = "";
	static String seqPublicacao = "";
	static String assunto = "";
	static String complementoAssunto = "";
	static String dummy = "";
	
	static String primeiraLinha = "";
	static String edtFolderName = "";
	static String secaoAnterior = "";
	
	static String atores = "";
	static String intimados = "";
	static String novoProcesso = null;
	static String processoDummy = "";
	static String processoLinhaPauta = "";
	static String ordemDePauta = "";
	
	static String linhaMensagem = "";
	static String linhaAnterior = "";
	static String linhaPauta = "";
	static String linhaFormatada = "";
	static String linhaParagrafo = "";
	static String linhaAntesAssunto = "";
	static String linhaDepoisLinhaProcesso = "";
	static String paginaPublicacao = "";
	static String numeroEdicao = "";
	static String nomeArqLog = "";
	static String linhaDaPauta = "";
	static String tribunaisSolicitados = "";
	
	static String processoLinha = "";
	static String pastaSaidaPDF = "";
	static String processoNumero = "";

	static String cliente;
	static String tipoConexao;
	static String sysOp;
	static String url;
	static String logFolder;

	static int paginaAtual = 1;
	static int pagina = 1;
	static int sequencialSaida = 1;
	static int maiorAssunto = 0;
	static int qtdPaginasDO = 0;
	static int sequencial = 0;	
	static int limiteGrupo = 0;
	static int sequencialSecao = 0;
	static int sequencialGrupo = 0;
	static int sequencialAssunto = 0;
	static int sequencialProcesso = 0;
	static int sequencialIndice = 0;
	static int linhaSumario = 0;
	static int indiceContador = 0;
	static int ultimaLinha = 0;
	static int ultimaPagina = 0;
	static int seqIndex = 0;
	static int sequencialSumario = 0;
	static int tamanhoLinha = 120;
	static int tamanhoLinhaAcumulado = 0;
	static int paginaGrupo = 0;
	static int paginaSecao = 0;
	static int totalPublicacoes = 0;
	static int tribunaisProcessados = 0;
	
	static boolean salvarIntroducao = false;
	static boolean limparIntro = true;
	static boolean mudouAssunto = false;
	static boolean saida = false;
	static boolean encontrouIndice;
	static boolean atoresOK = false;
	static boolean intimadosOK = false;
	static boolean pauta = false;
	static boolean dtValida = false;
	static boolean grupoSemAssunto = true;
	static boolean parametrizado = false;
	
	//static MsgWindow msgWindow = new MsgWindow();
	static InterfaceServidor conexao = new InterfaceServidor();
	static Configuracao configuracao = new Configuracao();
	static SijBulkImport bulkImport = new SijBulkImport();
	static SelecionaDO selecionaEdicao = new SelecionaDO();
	static BaixaConteudo baixaEdicoes = new BaixaConteudo();
	static Comuns comuns = new Comuns();
	
	static Session sessao;
	static SalvaPdf salvaPdf = new SalvaPdf();
	static GravaXml gravaXml = new GravaXml();
	static Edital publicacao = new Edital();
	static Base base = new Edital();

	static int k = 0;								// para testes
		
	public static void main(String[] args) throws Exception {
		String dummy = args.toString();
/*
		try {
			Comuns.apresentaMenssagem("Processamento com argumentos");
		} catch (ArrayIndexOutOfBoundsException aiofbex) {
			Comuns.apresentaMenssagem("Processamento sem argumentos");
        }
*/		

		if(args.length>0) {
			dummy = args[0];
		    parametros = dummy.split(",");
		    parametrizado = true;
		}
		inicializa(dummy);
	}
	
	public int batch(String param) throws Exception {
		nomeArqLog = param.replaceAll("[,]","-")+"-"+obtemHrAtual().replace(":", "");
		tribunaisProcessados = 0;
		parametros = param.split(",");
		parametrizado = true;
		inicializa(dummy);
		return tribunaisProcessados; 
    }

	public static void inicializa(String grupo) throws Exception {
		String nomeArquivo = "";
		nomeArqLog = grupo.replaceAll("[,]","-")+"-"+obtemHrAtual().replace(":", "");
		String idEdicao = "";
		Configuracao.carregaConfig();
		usuario = com.vconsulte.sij.base.Parametros.USUARIO;
		password = com.vconsulte.sij.base.Parametros.PASSWORD;
		pastaCarregamento = com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO;	
		tipoDocumento = com.vconsulte.sij.base.Parametros.TIPODOCUMENTO;
		versaoSplitter = com.vconsulte.sij.base.Parametros.VERSAOSPLITER;
		pastaSaida = com.vconsulte.sij.base.Parametros.PASTASAIDA;
		cliente = com.vconsulte.sij.base.Parametros.CLIENTE;	
		tipoConexao = com.vconsulte.sij.base.Parametros.CONEXAO;
		sysOp = com.vconsulte.sij.base.Parametros.SYSOP;
		url = com.vconsulte.sij.base.Parametros.URL;
		logFolder = com.vconsulte.sij.base.Parametros.LOGFOLDER;
		tipoProcessamento = com.vconsulte.sij.base.Parametros.TIPOPROCESSAMENTO;
		pastaEdicoes = com.vconsulte.sij.base.Parametros.PASTAEDICOES;
		tipoArquivoSaida = com.vconsulte.sij.base.Parametros.TIPOARQUIVOSAIDA;
//		pastaDeEdicoes = com.vconsulte.sij.base.Parametros.PASTADEEDICOES;
//		pastaIndiceXML = com.vconsulte.sij.base.Parametros.PASTAORIGEM;					em desenvolvimento
		pastaRelatorios = com.vconsulte.sij.base.Parametros.PASTARELATORIOS;
		
		List <String> edicoesNaoProcessadas = new ArrayList<String>();
		if(tipoProcessamento.equals("DESKTOP")) {
			MsgWindow msgWindow = new MsgWindow();
			msgWindow.montaJanela();
		}
		
		com.vconsulte.sij.base.Parametros.carregaTabelas();
		registraLog("Parametros Carregados");
		
		Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("SplitDO " + versaoSplitter + " - Separação de Publicações.", tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("\t\tParametros do processamento " , tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Grupo para processamento: " + grupo, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Pasta Carregamento: " + pastaCarregamento, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Tipo Documento: " + tipoDocumento, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Pasta Saida: " + pastaSaida, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Log Folder: " + logFolder, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Pasta Edicoes: " + pastaEdicoes, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Pasta de Edicoes PDF: " + pastaDeEdicoes, tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Pasta de Relatorios: " + pastaRelatorios, tipoProcessamento, "informativa", null);

		if(tipoConexao.equals("REMOTO")) {
			if (!conectaServidor()) { 
				Comuns.apresentaMenssagem("Falha na conexão com o Servidor.", tipoProcessamento, "erro",  null);
				gravaIndiceXML();
				finalizaProcesso();
			}
		}
		
		if(tipoConexao.equals("REMOTO")) {
			tabelaAssuntos = conexao.carregaAssuntosRemoto(sessao);
			Comuns.apresentaMenssagem("Tabela de assuntos carregada.", tipoProcessamento, "informativa", null);
		} else {
			carregaAssuntosLocal();
			registraLog("Tabela local de assuntos carregada");
		}

		if(tipoProcessamento.equals("DESKTOP")){
			registraLog("Processamento em modo Desktop");
			carregaAssuntosLocal();
			edicaoEscolhida = SelecionaDO.selecionaEdicao();
			processaEdicoes(listaEdicoes(pastaEdicoes, ".pdf"),edicaoEscolhida);
			Comuns.finalizaProcesso(tipoProcessamento);
		} else {
			if(tipoProcessamento.equals("BATCH")) {
				Comuns.apresentaMenssagem("Processamento em modo Batch", tipoProcessamento, "informativa", null);
				registraLog("Processamento em modo Batch");
				Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
				
				conectaServidor();

				if(!parametros[0].equals("servidor")){
					edicoesNaoProcessadas = InterfaceServidor.listaEdicoesNaoProcessadas(sessao);
					if(!edicoesNaoProcessadas.isEmpty()) {
						for(int ix=0; ix <= edicoesNaoProcessadas.size()-1; ix++) {
							if(parametros[0].equals("servidor")) {
								nomeArquivo = conexao.obtemNomeEdicaoPDF(sessao, edicoesNaoProcessadas.get(ix));
							} else {
								nomeArquivo = parametros[ix];
							}
							BaixaConteudo.baixaConteudo(sessao, edicoesNaoProcessadas.get(ix), nomeArquivo, pastaEdicoes);
							registraLog("\nDownload da edição: " + nomeArquivo + " realizado");
							Comuns.apresentaMenssagem("Download da edição: " + nomeArquivo + " realizado", tipoProcessamento, "informativa", null);
							edicoesProcessadas.add(edicoesNaoProcessadas.get(ix));
							tribunaisProcessados++;
						}
					} else {
						registraLog("Não houve edições para processar");
						resumoPublicacoes.add("Não houve edições para processar ");
						totalPublicacoesTribunal.add(tribunal + " 0");
						Comuns.apresentaMenssagem("Não houve edições para processar ", tipoProcessamento, "informativa", null);
					}
				}
				System.out.println("\n");
				processaEdicoes(listaEdicoes(pastaEdicoes, ".pdf"),null);
				if(parametros[0].equals("servidor") || parametros[0].equals("servidor")) {
					for(int ix=0;ix<=edicoesProcessadas.size()-1;ix++) {
						conexao.atualizaEdicaoProcessada(sessao, edicoesProcessadas.get(ix));
					}
				}
	//			gravaIndiceXML();
				registraLog("*** FIM DO PROCESSAMENTO DAS EDIÇÕES ***");
				Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
				if(!parametrizado) {
					finalizaProcesso();
				}
			}
		}
	}

	private static void processaEdicoes(File[] files, File edicaoEscolhida) throws Exception {
		String dummy = "";
		int qdtPublicacoes = 0;
		if(tipoProcessamento.equals("BATCH")){
			for (File file : files) {
				dummy = file.getName();
				if(!parametrizado) {
					if(file.getPath().contains("._")){
						continue;
					} else {
						registraLog("*** PROCESSAMENTO DA EDIÇÃO: " + dummy + " ***");
						Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
						Comuns.apresentaMenssagem("Arquivo: " + dummy, tipoProcessamento, "informativa", null);
						separaPublicacoes(file);
						qdtPublicacoes = sequencialSaida-1;
						resumoPublicacoes.add("Total de publicações processadas para o tribunal: " + strTribunal + " " + qdtPublicacoes + " publicações");
						totalPublicacoesTribunal.add(strTribunal + " " + qdtPublicacoes);
					}
				} else {	
					registraLog("*** PROCESSAMENTO DA EDIÇÃO: " + dummy + " ***");
					Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
					Comuns.apresentaMenssagem("Arquivo: " + dummy, tipoProcessamento, "informativa", null);						
					separaPublicacoes(file);
					qdtPublicacoes = sequencialSaida-1;
					resumoPublicacoes.add("Total de publicações processadas para o tribunal: " + strTribunal + " " + qdtPublicacoes + " publicações");
					totalPublicacoesTribunal.add(strTribunal + " " + qdtPublicacoes);
				}
			}
			System.out.println("\n");
		} else {
			separaPublicacoes(edicaoEscolhida);			
		}
		//Comuns.gravaLog(logFolder, nomeArqLog, "splitdo",log);
		Comuns.gravaLog(logFolder, "splitDo", "splitdo",log);	
		//Comuns.gravaArquivoTexto(logFolder, "spl" + tribunaisSolicitados + "-resumo.txt", resumoPublicacoes);
		gravaRelatorio();
		registraLog("gravando relatorios");
		Comuns.gravaArquivoTexto(logFolder, "spl-resumo.txt", resumoPublicacoes);
		Comuns.gravaArquivoTexto(logFolder, "publicacoes.txt", listaDePublicacoes);
		Comuns.gravaArquivoTexto(logFolder, "total-publicacoes-tribunal.txt", totalPublicacoesTribunal);
	}
	
	private static void gravaRelatorio() throws Exception {
		registraLog("Enviando relatorio ao servidor");
		Date dataAtual = new Date();
		String relatoriosNoServidor = pastaRelatorios;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String nomeRelatorio = "spl-resumo-" + dateFormat.format(dataAtual) + ".txt";
        String nomePasta = dateFormat.format(dataAtual);
        Folder pastaRelatorios = InterfaceServidor.verificaPastaRelatorios(sessao, relatoriosNoServidor, nomePasta, nomeRelatorio);
        InterfaceServidor.enviaRelatorio(sessao, pastaRelatorios, nomeRelatorio, resumoPublicacoes);
	}
	
	private static boolean verificaParametros(String parametro) {
		for (int i=0; i<parametros.length;i++) {
			if(parametros[i].equals(parametro)) {
				return true;
			}
		}
		return false;
	}
	
	private static File[] listaEdicoes(String path, final String extension) {
		String [] partesLinha;
		File dir = new File(path);
		FileFilter filter = null;
		if (extension != null) {
			filter = new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(extension);
				}
			};
		}
		return dir.listFiles(filter);
	}
	
	private static void inicializaEdicaoXML() throws Exception {
		registraLog(" Incializa edicaoXML \n");
		edicaoXML.add("<?xml version=\"1.0\" ?>");
		edicaoXML.add("<add>");
		edicaoXML.add("\t"+"<doc>");
	}
	
	private static String obterIdPastasDeEdicoes() throws Exception {
		return InterfaceServidor.getIdEdicoes(sessao, edtFolderName);
	}
	
	private static void atualizaPastasDeEdicoes() throws Exception {
		String pastaId = obterIdPastasDeEdicoes();
		InterfaceServidor.atualizaPastaEdicao(sessao, pastaId, "processado", "");
	}
	
	private static void formataBufferEntrada() {
		registraLog(" Inicio da formatação do buffer de entrada\n");
		Comuns.apresentaMenssagem("Início da fase de formatação", tipoProcessamento, "informativa", null);
		String dummy = "";
		for(int x = 0; x<=bufferEntrada.size()-1; x++) {					
			dummy = bufferEntrada.get(x);

			//------------------------------------------------------------------------------------------------------
			if(dummy.startsWith("Processo Nº")) {									// formatação de nº de processo
				if(!verificaSeLinhaTemNumProcesso(dummy) && somenteNumeros(bufferEntrada.get(x+1))) {
					if(verificaSeLinhaTemNumProcesso(bufferEntrada.get(x) + bufferEntrada.get(x+1))) {
						dummy = bufferEntrada.get(x)+bufferEntrada.get(x+1);
						x = x + 1;
					} else {
						dummy = bufferEntrada.get(x);
					}
					
				}
			}
			
			//------------------------------------------------------------------------------------------------------
			
			if((contaPalavras(dummy) == 1) && (dummy.equals("poder") && bufferEntrada.get(x+1).equals("judiciario"))) {	
				dummy = dummy + " " + bufferEntrada.get(x+1);
				x = x + 1;
			}
			
			//------------------------------------------------------------------------------------------------------
			
			bufferFormatado.add(dummy);
		}
		Comuns.apresentaMenssagem("Fim da fase de formatação", tipoProcessamento, "informativa", null);
		registraLog(" Fim da formatação do buffer de entrada\n");
	}
	
	private static boolean somenteNumeros(String linha) {
		String dummy = linha.replaceAll(".", "");
		for(int x = 0; x <= dummy.length()-1; x++) {
			if ((dummy.charAt(x) >= '0' && dummy.charAt(x) <= '9')){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private static void separaPublicacoes(File edicao) throws Exception {
		inicializaEdicaoXML();
		registraLog(" Inicio da Separação das Publicações\n");
		registraLog("*** Parametros do processamento ***");
		registraLog("\tPasta Carregamento: " + com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO);
		registraLog("\tTipo Documento: " + com.vconsulte.sij.base.Parametros.TIPODOCUMENTO);
		registraLog("\tVersao Splitter: " + com.vconsulte.sij.base.Parametros.VERSAOSPLITER);
		registraLog("\tPasta Saida: " + com.vconsulte.sij.base.Parametros.PASTASAIDA);
		registraLog("\tCliente: " + com.vconsulte.sij.base.Parametros.CLIENTE);	
		registraLog("\tTipo Conexao: "+ com.vconsulte.sij.base.Parametros.CONEXAO);
		registraLog("\tsysOp: " +com.vconsulte.sij.base.Parametros.SYSOP);
		registraLog("\turl: " + com.vconsulte.sij.base.Parametros.URL);
		registraLog("\tLog Folder: " +  com.vconsulte.sij.base.Parametros.LOGFOLDER);
		registraLog("\tTipo Processamento: " + com.vconsulte.sij.base.Parametros.TIPOPROCESSAMENTO);
		registraLog("\tPasta Edicoes: " + com.vconsulte.sij.base.Parametros.PASTAEDICOES);
		registraLog("\tTipo Arquivo Saida: " + com.vconsulte.sij.base.Parametros.TIPOARQUIVOSAIDA);
		registraLog("\tPasta de Edicoes: " + com.vconsulte.sij.base.Parametros.PASTADEEDICOES+"\n");
		registraLog("separaPublicacoes - Início");
		Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);

		String strDummy = "";
		String dummy = "";
		String linha = "";
		String linhaDummy = "";

		boolean salvarLinha = false;
		boolean primeiroEdital = true;
		boolean quebrouAssunto = false;
		boolean ignora = false;
		pagina = 1;
		
		try {
			if(sequencialSaida > 1) totalPublicacoes = totalPublicacoes + sequencialSaida;
			
			tribunal = "";
			strTribunal = "";
			strEdicao = "";
			edtFolderName = "";
			seqEdicao = "";
			sequencial = 0;
			sequencialSaida = 1;
			textoEdital.clear();
			textoSaida.clear();
			textoIntroducao.clear();
			edital.clear();
			introducao.clear();
			paragrafos.clear();
			bufferEntrada.clear();
			if(!carregaEdicao(edicao)){
				Comuns.apresentaMenssagem("Edição do Diário Oficial não contém publicações.", tipoProcessamento, "informativa", null);
			} else {
				bufferFormatado.clear();
				formataBufferEntrada();
				carregaIndice();
				mapeiaLinhas();		
 		        if (bufferFormatado.get(0).contains("Caderno Judiciário")){
		        	dummy = "";
		        	tribunal = obtemTribunal(bufferFormatado.get(0));		
			    	strTribunal = (completaEsquerda(obtemTribunal(bufferFormatado.get(0)), '0', 2));
			    	titulo1 = bufferFormatado.get(0).trim(); 
				    titulo2 = bufferFormatado.get(1).trim();
			        titulo3 = bufferFormatado.get(2).trim();
			    
			        numeroEdicao = primeiraPalavra(bufferFormatado.get(3));
	
			        dummy = obtemEdicao(bufferFormatado.get(3));
			        strEdicao = dummy + "T00:00:00.000-03:00";
			        titulo4 = bufferFormatado.get(3).trim();
			        seqEdicao = bufferFormatado.get(3).trim().substring(2, 6);
			        
			        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			        sdf.setLenient(false);
			        DtEdicao = sdf.parse(dummy);
			        
			        titulo5 = bufferFormatado.get(4).trim();
					if (strTribunal.equals("00")) {
						Comuns.apresentaMenssagem("\n" + "TST -"+" Edição: " + dataEdicao, tipoProcessamento, "informativa", null);
						edtFolderName = "\n" + "TST - " + seqEdicao;
						descricaoFolder = "\n" + "TST - Edição:" + dataEdicao;
					} else {
						Comuns.apresentaMenssagem("Início da separação das publicações (TRT "+strTribunal+"ª Região "+" Edição: " + dataEdicao + ")", tipoProcessamento, "informativa", null);
						edtFolderName = "TRT - " + strTribunal + "-" + seqEdicao;
						descricaoFolder = "TRT " + strTribunal + "ª Região" + " - " + dataEdicao;
					}
		        } else {
		        	Comuns.apresentaMenssagem("Arquivo com Diário Oficial não reconhecido", tipoProcessamento, "informativa", null);
					finalizaProcesso();    	
		        }
        
		        edtFolderName = "TRT " + strTribunal + " " + dataEdicao.replaceAll("[/]","-");
		        if(tipoConexao.contentEquals("LOCAL")) {
			        pastaSaidaPDF = edtFolderName.replaceAll(" ", "-");
			        diretorio = new File(pastaSaida + "/" + pastaSaidaPDF);
			        criaPastaSaidaPDF(diretorio);		        
			        publicacao.setStrEdicao(strEdicao);
			        publicacao.setTribunal(completaEsquerda(tribunal,'0',2));
			        publicacao.setFolderName(pastaSaidaPDF);
			        GravaXml.main(pastaSaida + "/" + pastaSaidaPDF, false, dataEdicao.replace("/", "-"));
		        }
	        
		        File dir1 = new File (".");
		        registraLog("ini -> " + tribunal + " - " + descricaoFolder);
		        
// Loop de indice (percorre o indice do documento)			        
		        
		        for (IndiceEdicao Indice : Index) {					// Loop de indice (percorre o indice do documento)	
	
		        	if(linha.equals("*** MARCA FIM ***")){
						break;
					}	        	
		        	
		        	registraLog("separaPublicacoes - Início do loop do Índice");
		        	// NOVA SESSÃO e GRUPO ---------------------------------------------------------------------------------
		        	//if(!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0)) {
		        	if((textoEdital.size() > 0 || paragrafos.size() > 0)) {
						seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);
						edital = formataPublicacao(textoEdital);
						fechaPublicacao((ArrayList<String>) edital);
					}
		        	
		    		secao = bufferFormatado.get(Indice.linhaSecao).trim();
		    		sequencialSecao = Indice.linhaSecao;
		    		paginaSecao = Indice.paginaSecao;
		    		registraLog("-----------------------------------------------------------------------------");
		    		registraLog("*** Início da publicação nº " + sequencialSaida + " iniciado. ***");
		    		registraLog("Seção -> " + secao + " - sequencial: " + sequencial);
		    		if(Indice.complementoSecao != null && !Indice.complementoSecao.equals("complemento")) {
						secao = secao + " " + Indice.complementoSecao;
					}
		    		padraoGrupo.clear();
		    		grupo = bufferFormatado.get(Indice.linhaGrupo).trim();
		    		paginaGrupo = Indice.paginaGrupo;
		    		padraoGrupo.add("grupo");
		    		sequencial = Indice.linhaGrupo + 1;
		    		sequencialGrupo = Indice.linhaGrupo;
		    		textoIntroducao.clear();
		    		textoEdital.clear();        		
		    		assunto = "";
		    		processoLinha = "";
		    		atores = "";
		    		intimados = "";
		    		salvarIntroducao = false;
		    		pauta = false;
		    		linhaPauta = "";
		    		ordemDePauta = "";
		    		linhaAntesAssunto = "";
		    		linha = "";
		    		if(grupo.equals("Pauta")) {
		    			linhaPauta = "";
		    			processoLinhaPauta = "";
		    			pauta = true;
		    			salvarIntroducao = true;
		    		}
		    		
		    		linhaDummy = carregaLinha(sequencial,false,716);
		    		if((obtemNumeroProcesso(linhaDummy) != null)){
		    			grupoSemAssunto = true;
		    		} else {
		    			if(validaAssunto(linhaDummy)) {
		    				grupoSemAssunto = false;
		    			}
		        	}
		    		
		    		//Comuns.apresentaMenssagem("Grupo: " + grupo);
		    		registraLog("Grupo -> "+ grupo + " - sequencial: " + sequencial + " - pg: " + Indice.paginaSecao + " / " + ultimaPagina);
		    		limiteGrupo = localizaProximoGrupo(sequencial);
		    		if(limiteGrupo == -1) limiteGrupo = ultimaLinha;
					assunto = "";
					quebrouAssunto = false; 
		    		indiceContador++;
		    		
					if(tipoConexao.equals("REMOTO")) inicializaPublicacao();
					
			// Início do loop de linhas de assunto corpo  -------------------------------------------------------------------	   
					
					registraLog("Início do loop de linhas");			
					while((sequencial < limiteGrupo)) {
	
						if(textoEdital.size() == 0) {
		    				registraLog("***    Inicio de publicação    ***");
		    			}
						linhaDummy = "";
		        		salvarLinha = true;
		        		linhaAnterior = linha;
		        		linha = carregaLinha(sequencial,true,745);

     		
/*   System.out.println(sequencial);		   
   if(sequencial >= 17 ) {
 	   k++;
   }
   if(sequencial >= 50) {
	   k++;
   }*/
   if(sequencial >= 3281) {	
	   k++;
   } 
   if(sequencial >= 3253) {
	   k++;
   } 

		        		if(linha.equals("*** MARCA FIM ***")){
							break;
						}
		        		
		        		if(linha.startsWith("Processo Nº") || linha.startsWith("PROC. Nº")) {
		        			dummy = "";
		        			dummy = obtemNumeroProcesso(linha);
		        			if(dummy == null) {
		        				linha = linha + carregaLinha(sequencial,true,745);
		        			}
		        		}
		        		
		        		if(linha.equals("PROC. Nº")) {
		        			dummy = "";
		        			linhaDummy = carregaLinha(sequencial,false,745);
		        			dummy = obtemNumeroProcesso(linhaDummy);
		        			if(!dummy.isEmpty()) {
		        				linha = linha + " " + linhaDummy;
		        				sequencial++;
		        			}
		        		}
	
		        		if(verificaSeLinhaTemNumProcesso(linha)) {
		        			processoDummy = obtemNumeroProcesso(linha);
		        			if(processoNumero == "") {
		        				processoNumero = processoDummy;
		        			//	processoDummy = null;					// para força a não quebra da publicação
		        			}
		        		} else {
		        			processoDummy = null;
		        		}

		        		linhaDummy = formataPalavra(linha.replaceAll("[:0123456789]",""));
		        		linhaDummy = linhaDummy.trim();
	
		        		if(sequencial >= sequencialSumario) {
			        		for (IndiceEdicao Ix1 : Index) {
			        			if(sequencial == Ix1.indexSecao || sequencial == Ix1.indexGrupo) {
			        				ignora = true;
			        				break;
			        			}
			        		}
		        		}
		        		if(ignora) {
		        			ignora = false;
		        			continue;
		        		}
		        		
		        		if(linha.equals("*** MARCA FIM ***")) {
		        			seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);
		    				edital = formataPublicacao(textoEdital);
		    				fechaPublicacao((ArrayList<String>) edital);
		        			break;
		        		}
	
						/*	
						 * Quebra por Assunto
						 * 
						 */
		
		        		// if((tabelaAssuntos.contains(formataPalavra(primeiraPalavra(linha))) && !grupoSemAssunto) || (linhaDummy.equals("ordem"))) {
		        		if((validaAssunto(linha) && !grupoSemAssunto) || validaExcecaoAssunto(formataPalavra(linha))) {
		        			//if((quebraAssunto(sequencial-1,limiteGrupo) && !salvarIntroducao) || (linhaDummy.equals("ordem") && salvarIntroducao)) {
		        			if((quebraAssunto(sequencial-1,limiteGrupo) ) || (linhaDummy.equals("ordem") && salvarIntroducao)) {
		        				registraLog("quebra por assunto identificada");
		        				if(paginaPublicacao.isEmpty()) paginaPublicacao = completaEsquerda(Integer.toString(pagina), '0', 6);
		        				if(!padraoGrupo.contains("assunto")) {
									padraoGrupo.add("assunto");
								}
		        				
		        				if((!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0) || textoIntroducao.size() > 0) &&
		        						!processoLinha.isEmpty()){
									seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);								
			        				edital = formataPublicacao(textoEdital);
			        				fechaPublicacao((ArrayList<String>) edital);	
			        				ordemDePauta = "";
			        				linhaAntesAssunto = "";
								} else {
									primeiroEdital = false; 
								}
								
								if(linhaDummy.equals("ordem")) {
									quebrouAssunto = false; 
									ordemDePauta = linha;
								} else {
									quebrouAssunto = true;
									assunto = linha;
								}
								
								sequencialAssunto = sequencial-1;
								linhaPauta = "";		
								if(!verificaSeLinhaTemNumProcesso(carregaLinha(sequencial,false,821))) {
									if(!textoIntroducao.isEmpty() && salvarIntroducao) {
										textoIntroducao.clear();
										salvarIntroducao = false;
										registraLog("Limpou texto introdução apagado.");
										registraLog("Flag salvarIntrodução desligado.");
									} else {					
										if(!verificaSeLinhaTemNumProcesso(carregaLinha(sequencial+1,false,909)) || formataPalavra(assunto).equals("pauta de julgamento")) {
											textoIntroducao.clear();
											salvarIntroducao = true;
											registraLog("Assunto igual a Pauta de Julgamento");
											registraLog("Texto introdução limpo para novo texto.");
										} else {
											if(textoIntroducao.isEmpty()) {
												registraLog("Salvar introdução ligado.");
												salvarIntroducao = true;
												linhaPauta = "";
											}
										}
									}
								} else {
									registraLog("Flag salvarIntrodução desligado.");
									salvarIntroducao = false;
								}
								continue;
		        			}

						} else { 
							if (sequencial-2 == sequencialGrupo && (grupo.equals("Pauta") && !validaAtor(linha))) {
								linhaAntesAssunto = linha;
								continue;
							}
						}
		
						/*
						 * Tratamento de introdução do Edital quando houve
						 * (introdução é um bloco de texto comum a vários editais de um mesmo assunto
						 * 
						 */
						if(salvarIntroducao){
						//	if(processoDummy == null) {		
							if(!verificaSeLinhaTemNumProcesso(linha)) {
								if(!padraoGrupo.contains("introducao")) {
									padraoGrupo.add("introducao");
								}
								
								if(linhaDummy.equals("ordem")) {
									ordemDePauta = linha;
									continue;
								}
								textoIntroducao.add(linha);
								continue;
							} else {
								salvarIntroducao = false;
							}
						}
						/*
						 * Quebra por Nº PROCESSO
						 */
						if(processoDummy != null) {
						//if(verificaSeLinhaTemNumProcesso(linha)) {
							if(quebraProcesso(sequencial-1)) {	
								registraLog("quebra por processo identificada");
								if(paginaPublicacao.isEmpty()) paginaPublicacao = completaEsquerda(Integer.toString(pagina), '0', 6);
								if(!padraoGrupo.contains("processo")) {
									padraoGrupo.add("processo");
								}								
								novoProcesso = linha;
								if(!pauta) {					  				// se ñ é pauta a quebra é por assunto      								
									if(!primeiroEdital && (textoEdital.size() > 0 || paragrafos.size() > 0)) {
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);
				        				edital = formataPublicacao(textoEdital);		        				
				        				fechaPublicacao((ArrayList<String>) edital);
									}						
								} else {										// se assunto = pauta a quebra é por nº processo
									if(!atores.isEmpty() || !intimados.isEmpty()){
										seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);
				        				edital = formataPublicacao(textoEdital);
				        				fechaPublicacao((ArrayList<String>) edital);
									}
								}
								if(primeiroEdital) {
									primeiroEdital = false;
								}
								salvarLinha = false;
								processoLinha = linha;
								processoNumero = processoDummy;
								sequencialProcesso = sequencial-1;
								salvarIntroducao = false;
							}							
						} 

						if(!atoresOK && !processoLinha.isEmpty() && atores.isEmpty()){
							if(validaAtor(linha)) {
								atores = trataAtores(linha);
								if(!padraoGrupo.contains("atores")) {
									padraoGrupo.add("atores");
								}
								
							} else if(processoDummy == null){
								linhaDepoisLinhaProcesso = linha;
							}
							continue;
						}
						
						if(!atoresOK && !processoLinha.isEmpty()){
							intimados = trataIntimados(linha);
		        			if(!padraoGrupo.contains("intimados")) {
								padraoGrupo.add("intimados");
							}
		        			atoresOK = true;
							continue;
						}
						
						/*
						 * Guarda linha do texto do Edital
						 */	
						
						if(textoEdital.size() == 0) {
		    				primeiraLinha = linha;
		    			}     
		    			if(salvarLinha) {
		    				if(!padraoGrupo.contains("texto")) {
								padraoGrupo.add("texto");
							}
		    				textoEdital.add(linha);
		    			}
		        		salvarLinha = true;
		        		strDummy = "";	
		        	} 	// fim do WHILE de linhas ------------------------------------------------------------------------------
					
	// fim do while
					
					
					registraLog("Fim do loop de linhas");	        
		        }	// fim do FOR do Indice
	        
// fim do for do indice

		        registraLog("Fim do loop do Índice");
				if(textoEdital.size() > 0 || paragrafos.size() >0){ 
					registraLog("Finalização da edição");
				    seqPublicacao = completaEsquerda(Integer.toString(sequencialSaida), '0', 6);
					edital = formataPublicacao(textoEdital);
					paginaPublicacao = completaEsquerda(Integer.toString(pagina), '0', 6);
					fechaPublicacao((ArrayList<String>) edital);
					edital.clear();
					textoEdital.clear();
				}
			}
	}	 					// <==== Fim do try existente no método processaEdicao
	
	catch (IOException erro) {

		Comuns.apresentaMenssagem("Erro no processamento do intermedio: ", tipoProcessamento, "erro",  erro.toString());
	}

	Comuns.apresentaMenssagem("Fim do processamnto da edição: " + edicao, tipoProcessamento, "informativa", null);
	Comuns.apresentaMenssagem("------------------------------------------------------------------------------\n\n", tipoProcessamento, "informativa", null );	
	renomeiaEdicao(edicao);
	//System.exit(0);
	}						// <==== fim do método processaEdicao
	
	private static void renomeiaEdicao(File arquivo) {
		File arquivo2 = new File(arquivo + "_rep" );
		arquivo.renameTo(arquivo2);
	}

	private static boolean verificaMaiuscula(String linhaDummy) {
		String dummy = linhaDummy.replaceAll("[:.-]","");
		int minusculos = 0;
		//int maiusculos = 0;
		double percentual = 0.0;
		dummy = dummy.replaceAll("[ÁÀÃÂÄÅ]","A");
		dummy = dummy.replaceAll("[ÉÈÊË]","E");
		dummy = dummy.replaceAll("[ÍÌÎÏ]","I");
		dummy = dummy.replaceAll("[ÓÒÔÖÕ]","O");
		dummy = dummy.replaceAll("[ÚÙÛÜ]","U");
		dummy = dummy.replaceAll("[Ç]","C");
		dummy = dummy.replaceAll("[()]","");
		dummy = dummy.replaceAll("[' ']","");
		dummy = dummy.replaceAll("[ªº]","");
		dummy = dummy.replaceAll("[0123456789]","");
		for(int x = 0; x <= dummy.length()-1; x++) {

			if ((dummy.charAt(x) >= 'a' && dummy.charAt(x) <= 'z') ||
					dummy.charAt(x) == 'ã' || dummy.charAt(x) == 'á' ||dummy.charAt(x) == 'à' || dummy.charAt(x) == 'â' || 
					dummy.charAt(x) == 'é' || dummy.charAt(x) == 'è' || dummy.charAt(x) == 'ê' ||
					dummy.charAt(x) == 'í' || dummy.charAt(x) == 'ì' || dummy.charAt(x) == 'î' ||
					dummy.charAt(x) == 'ó' || dummy.charAt(x) == 'ò' || dummy.charAt(x) == 'ô' || dummy.charAt(x) == 'õ' ||
					dummy.charAt(x) == 'ú' || dummy.charAt(x) == 'ù' || dummy.charAt(x) == 'û'){
				minusculos++;
			}
		}
		if(minusculos > 0) {
			percentual = (float) (minusculos*100/dummy.length());
		} else {
			return true;
		}
		
		if(percentual >= 5.0) {
			return false;
		} else {
			return true;
		}
		
	}
	
	private static boolean ehMaiuscula(String linhaDummy) {
		String dummy = linhaDummy.replaceAll("[:.]","");
		dummy = dummy.replaceAll("[AÁÀÃÂÄÅ]","A");
		dummy = dummy.replaceAll("[EÉÈÊË]","E");
		dummy = dummy.replaceAll("[IÍÌÎÏ]","I");
		dummy = dummy.replaceAll("[OÓÒÔÖÕ]","O");
		dummy = dummy.replaceAll("[UÚÙÛÜ]","U");
		dummy = dummy.replaceAll("[()]","");
		dummy = dummy.replaceAll("[' ']","");
		dummy = dummy.replaceAll("[ªº]","");
		for(int x = 0; x <= dummy.length()-1; x++) {
			if ((dummy.charAt(x) >= 'A' && dummy.charAt(x) <= 'Z') ||
					(dummy.charAt(x) >= '0' && dummy.charAt(x) <= '9')){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static boolean temContinuacao(String linhaDummy) {
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];                		
		palavras = linhaDummy.split(" ");
		linhaDummy = formataPalavra(linhaDummy);
		if(continuadores.contains(palavras[palavras.length-1])) {
			return true;
		}
		return false;
	}
	
	private static String separaAtores(String linhaDummy) {
		
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String atoresSeparados = "";
		String linhaDeAtores = "";
		String var2[] = new String[var];                		
		var2 = linhaDummy.split(" ");
		for (int x = 0; x <= var2.length-1; x++) {
			if(var2[x].charAt(var2[x].length()-1) == ';' || var2[x].charAt(var2[x].length()-1) == ',' ){
				if(atoresSeparados.isEmpty()) {
					atoresSeparados = var2[x] + "\n";
				} else {
					atoresSeparados = atoresSeparados + " " + var2[x] + "\n";
				}
				linhaDeAtores = "";
			} else {
				if(linhaDeAtores.isEmpty()) {
					linhaDeAtores = var2[x];
				} else  {
					linhaDeAtores = linhaDeAtores + " " + var2[x];
				}
			}
		}
		return atoresSeparados;
	}

	private static void salvaLinha(String linhaDummy) {
		//String nada = "";
		//String dummy = obtemNumeroProcesso(linhaDummy);
		String linhaFormatada = formataPalavra(linhaDummy);
		char ponto = linhaDummy.charAt(linhaDummy.length()-1);
		boolean incioMaiuscula = false;
		
		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		String palavra = "";
		palavras = linhaDummy.split(" ");

		if(linhaDummy.charAt(0) >= 'A' && linhaDummy.charAt(0) <= 'Z') {
			incioMaiuscula = true;
		}
				
		if(obtemNumeroProcesso(linhaDummy) != null){							// verifica se linha tem nº processo
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo);
				paragrafos.add(linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} else {
				paragrafos.add(linhaDummy + "\n");
			}
		} else if(verificaDataFinal(linhaDummy, sequencial)) {					// Linha com data Valida
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(juridiques.contains(formataPalavra(linhaDummy))) {			// Linha com juridiques
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(funcoes.contains(linhaFormatada)) {							// Linha com função 
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy);
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy + "\n");
		} else if(linhaFormatada.equals("poder judiciario")) {					// poder judiciario
			if(!linhaParagrafo.isEmpty()) {
				paragrafos.add(linhaParagrafo + " " + linhaDummy + "\n");
				linhaParagrafo = "";
				tamanhoLinhaAcumulado = 0;
			} 
			paragrafos.add(linhaDummy);											// Maiuscula + Atores
		} else if(ehMaiuscula(linhaDummy) && tabelaAtores.contains(primeiraPalavra(formataPalavra(linhaDummy)))) {
			if(!linhaParagrafo.isEmpty()) {
				if(tamanhoLinhaAcumulado > tamanhoLinha) {
					paragrafos.add(quebraLinha(linhaParagrafo));
				} else {
					paragrafos.add(linhaParagrafo);
				}
			} 
			linhaParagrafo = linhaDummy;
			tamanhoLinhaAcumulado = linhaDummy.length();
		} else if(linhaFormatada.equals("poder") || linhaFormatada.equals("judiciario")) { 	// PODER ou JUDICIARIO
		     if(linhaFormatada.equals("poder")) {
		       if(!linhaParagrafo.isEmpty()) {
		    	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}
		         linhaParagrafo = linhaDummy;
		         tamanhoLinhaAcumulado = linhaDummy.length();
		       } else {
		         linhaParagrafo = linhaDummy;
		         tamanhoLinhaAcumulado = linhaDummy.length();
		       }
		     } else {
		       if(!linhaParagrafo.isEmpty() && linhaParagrafo.equals("PODER")) {
		    	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo + " " +linhaDummy));
					} else {
						paragrafos.add(linhaParagrafo + " " +linhaDummy);
					}
		         linhaParagrafo = "";
		         tamanhoLinhaAcumulado = 0;
		       }
		     }
		} else if(ehMaiuscula(linhaDummy)) {											// Começa maiúscula
	       if(tabelaAtores.contains(primeiraPalavra(formataPalavra(linhaAnterior)))) {
	           for (int x = 0; x <= palavras.length-1; x++) {
	        	   if(palavras[x].isEmpty()) {
						palavras[x] = " ";
					}
	             if(palavras[x].charAt(palavras[x].length()-1) == ',' ||
	                 palavras[x].charAt(palavras[x].length()-1) == ';'){
	               linhaParagrafo = linhaParagrafo + " " + palavras[x] + "\n";
	               tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
	             } else if(palavras[x].charAt(palavras[x].length()-1) >= 'A' || 
	                 palavras[x].charAt(palavras[x].length()-1) <= 'Z' ||
	                 palavras[x].charAt(palavras[x].length()-1) >= 'a' ||
	                 palavras[x].charAt(palavras[x].length()-1) <='z') {
	               if(linhaParagrafo.isEmpty()) {
	                 linhaParagrafo = palavras[x];
	                 tamanhoLinhaAcumulado = palavras[x].length();
	               } else {
	                 linhaParagrafo = linhaParagrafo + " " + palavras[x];
	                 tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
	               }
	             } else if(palavras[x].charAt(palavras[x].length()-1) == ':') {
	               palavra = palavra + palavras[x];
	               if(tamanhoLinhaAcumulado > tamanhoLinha) {
	            	   paragrafos.add(quebraLinha(linhaDummy));
	               } else {
	            	   paragrafos.add(linhaDummy);
	               }
	               linhaParagrafo = palavra;
	               tamanhoLinhaAcumulado = palavra.length();
	             }
	           }
	         } else {
	           if(!linhaParagrafo.isEmpty()) {
	        	   if(tamanhoLinhaAcumulado > tamanhoLinha) {
	        		   paragrafos.add(quebraLinha(linhaParagrafo + " " + linhaDummy));
	        	   } else {
	        		   paragrafos.add(linhaParagrafo + " " + linhaDummy);
	        	   }
	             linhaParagrafo = "";
	             tamanhoLinhaAcumulado = 0;
	           } else {
	             paragrafos.add(linhaDummy);
	           }
	         }
		} else if(incioMaiuscula) {													// Começo é maiusculo
			if(juridiques.contains(linhaFormatada) && !verificaDataValida(linhaFormatada)) {
				if(!linhaParagrafo.isEmpty()) {
					if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}
					linhaParagrafo = "";
					tamanhoLinhaAcumulado = 0;
				} 
				paragrafos.add(linhaDummy);
			} else {
				for (int x = 0; x <= palavras.length-1; x++) {
					if(palavras[x].isEmpty()) {
						palavras[x] = " ";
					}
					if(ehMaiuscula(palavras[x]) && palavras[x].charAt(palavras[x].length()-1) == '.'){
						paragrafos.add(quebraLinha(linhaParagrafo + " " + palavras[x]));
						linhaParagrafo = "";
						tamanhoLinhaAcumulado = 0;
			        } else {
			        	if(ehMaiuscula(palavras[x])) {
			        		if(linhaParagrafo.isEmpty()) {
			        			linhaParagrafo = palavras[x];
			        			tamanhoLinhaAcumulado = palavras[x].length();
			        		} else {
			        			linhaParagrafo = linhaParagrafo + " " + palavras[x];
			        			tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
			        		}
				        } else {
				        	if(!linhaParagrafo.isEmpty()) {
				        		linhaParagrafo = linhaParagrafo + " " + palavras[x];
				        		tamanhoLinhaAcumulado = tamanhoLinhaAcumulado + palavras[x].length() + 1;
					        } else {
					        	linhaParagrafo = palavras[x];
					        	tamanhoLinhaAcumulado = palavras[x].length();
					        }
				        }
			        }
				}

				if(!linhaParagrafo.isEmpty() && ponto == '.') {
					if(tamanhoLinhaAcumulado > tamanhoLinha) {
						paragrafos.add(quebraLinha(linhaParagrafo));
					} else {
						paragrafos.add(linhaParagrafo);
					}					
					linhaParagrafo = "";
					tamanhoLinhaAcumulado = 0;
				}
			}
		} else if(!incioMaiuscula) {												// tudo minusculo
			if(linhaParagrafo.isEmpty()) {
    			linhaParagrafo = linhaDummy;
    			tamanhoLinhaAcumulado = linhaDummy.length();
    		} else {
    			linhaParagrafo = linhaParagrafo + " " + linhaDummy;
    		}
		}
		if(!linhaParagrafo.isEmpty() && linhaParagrafo.charAt(linhaParagrafo.length()-1) == '.') {
			paragrafos.add(quebraLinha(linhaParagrafo) + "\n");
			linhaParagrafo = "";
			tamanhoLinhaAcumulado = 0;
		}
	}

	private static String obtemData(String linhaDummy) {
		String linhaData = linhaDummy;
		String dataFinal = "";
		String dummy = "";
		int num = 0;
		int var = linhaData.split(" ", -1).length - 1;
		String linhaDecomposta[] = new String[var];

		if(linhaData.charAt(linhaData.length()-1) == '.') {
			linhaData = linhaData.substring(0, linhaData.length()-1);
		}
		linhaDecomposta = linhaData.split(" ");
		if(linhaDecomposta.length >= 6) {
			for(int i=0; i <= linhaDecomposta.length-1; i++) {
				dummy = linhaDecomposta[i].replaceAll("[.,]","");
				if(dummy.length() == 2) {
					if(ehInteiro(dummy)) {
						num = Integer.parseInt(dummy);
						if((num >= 1 && num <= 31) || (num >= 1 && num <= 12)) {
							if(dataFinal.isEmpty()) {
								dataFinal = dummy;
							} else {
								dataFinal = dataFinal + "-" + dummy;
							}
						}
					}
				}
				if(dummy.length() == 4 && ehInteiro(dummy)) {
					dataFinal = dataFinal + "-" + dummy;
					break;
				}
				if(meses.contains(linhaDecomposta[i])) {
					switch(linhaDecomposta[i]) {
					case "janeiro":
						dataFinal = dataFinal + "-" + "01";
						break;
					case "fevereiro":
						dataFinal = dataFinal + "-" + "02";
						break;
					case "marco":
						dataFinal = dataFinal + "-" + "03";
						break;
					case "abril":
						dataFinal = dataFinal + "-" + "04";
						break;
					case "maio":
						dataFinal = dataFinal + "-" + "05";
						break;
					case "junho":
						dataFinal = dataFinal + "-" + "06";
						break;
					case "julho":
						dataFinal = dataFinal + "-" + "07";
						break;
					case "agosto":
						dataFinal = dataFinal + "-" + "08";
						break;
					case "setembro":
						dataFinal = dataFinal + "-" + "09";
						break;
					case "outubro":
						dataFinal = dataFinal + "-" + "10";
						break;
					case "novembro":
						dataFinal = dataFinal + "-" + "11";
						break;
					case "dezembro":
						dataFinal = dataFinal + "-" + "12";
						break;
					}
				}
			}
		} else {
			dummy = linhaData.replaceAll("[ABCDEFGHIJKLMNOPQRSTUVXZWYabcdefghijklmnoprstuvxzwy]","").trim();
			dummy = dummy.replaceAll("[/]","-");
		}
		return dataFinal;
	}
	
	private static boolean verificaDataReduzida(String data) {
		registraLog("Verificando data reduzida. (verificaDataReduzida) - " + sequencial);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.parse(data);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
	
	private static boolean verificaDataValida(String linhaDummy){
		/**
		 * Verifica se a linha tem uma data válida no formato extenso (DD de MMMMM de AAAA)
		 */
		//String linhaDummy = dataTeste;
		String localidade = "";
		String dia = "";
		String mes = "";
		String ano = "";
		String linhaData = "";
		String dummy = formataPalavra(linhaDummy);
		dummy = dummy.replaceAll("[.]","");
		
		//int var = dummy.split(" ", -1).length - 1;
		//String palavras[] = new String[var];
		
		int var = dummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		palavras = dummy.split(" ");		
		
		if(!verificaSeLinhaTemNumProcesso(linhaDummy) && !validaAssunto(linhaDummy) && 
				!verificaStopWords(linhaDummy) && !validaAtor(linhaDummy) &&
				verificaMeses(linhaDummy)){
			
			if(!verificaDataReduzida(linhaDummy)) {
				for(int x=0; x <= palavras.length-1; x++) {

					if(!palavras[x].isEmpty()) {
						if(verificaLetras(palavras[x])) {	// loop para achar municipio da data por extenso
							if(!localidade.endsWith(",")) {
								if(palavras[x].indexOf("/") != 0 && palavras[x].charAt(palavras[x].length()-1) == ',') {
									if(localidade.isEmpty()) {
										localidade = palavras[x];
									} else {
										localidade = localidade + " " + palavras[x];
									}
								} else {
									if(localidade.isEmpty()) {
										localidade = palavras[x];
									} else {
										localidade = localidade + " " + palavras[x];
									}
								}
							}
							if(palavras[x].equals("de")){
								continue;
							} else {
								if(meses.contains(palavras[x])) {
									mes = palavras[x];
								}
								continue;
							}
						}
					} else {
						continue;
					}
					if(palavras[x].length() <=2) {
						dia = completaEsquerda(palavras[x], '0', 2);
					} else if(palavras[x].length() == 4) {
						ano = palavras[x];
					}
				}
				
				linhaData = dia + " " + mes + " " + ano;
				
				if(linhaData.matches("\\d{2}\\s\\w{4}\\s\\d{4}")) {					// Maio
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{5}\\s\\d{4}")) {			// Março, Abril, Junho, Julho
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{6}\\s\\d{4}")) {			// Agosto
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{7}\\s\\d{4}")) {			// Janeiro, Outubro
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{8}\\s\\d{4}")) {			// Setembro, Novembro, Dezembro
					return true;
				} else if(linhaData.matches("\\d{2}\\s\\w{9}\\s\\d{4}")) {			// Fevereiro
					return true;
				} else if(linhaDummy.startsWith(localidade) && linhaDummy.endsWith(ano + ".")) {
					return true;
				}
			} else {
				return true;
			}
		}	
		return false;
	}
	
	private static boolean verificaLetras(String palavra) {
		Pattern pattern = Pattern.compile("[0-9]");
		Matcher match = pattern.matcher(palavra);
		if(match.find()) { 
			return false;
		} else {
			return true;
		}
	}
	
	private static boolean procuraIntimados() {
		int inicio = sequencial;
		String linhaDummy = "";

		for (int x=0; x <= 50; x++) {
			linhaDummy = formataPalavra(carregaLinha(inicio, false,1522));
			if(linhaDummy.equals("intimado(s)/citado(s):") || linhaDummy.equals("PODER JUDICIÁRIO")){
				return true;
			}
			if(verificaDataFinal(linhaDummy, sequencial)) {
				saida = true;
			}
		}
		return false;
	}
	
	private static boolean avaliaMudancaProcesso(int indice) {	// Avalia se  linha lida tem um nº de processo que 
		String linhaDummy = carregaLinha(indice,false,1534);			// é provavel quebre de publicação
		String dummy = "";										// true = provavel quebra de processo 
		String dummy1 = "";										// false = apenas uma linha com nº de processo

		dummy = obtemNumeroProcesso(linhaDummy);
		registraLog("Avaliação de mudança de processo iniciado (avaliaMudancaProcesso) - " + sequencial);
		if(dummy.equals(processoNumero)) {				// compara proc lido com proc da publicação corrente
			if(linhaDummy.equals(processoLinha)) {
				for(int x=indice+1; x <= indice+8; x++) {
					dummy1 = carregaLinha(x,false,1543);
					if(validaAtor(dummy1)){
						registraLog("avaliação positiva de mudança de processo");
						return true;
					}
					if(dummy1.equals("Intimado(s)/Citado(s):")){
						registraLog("avaliação positiva de mudança de processo");
						return true;
					}
					if(dummy1.equals("PODER JUDICIÁRIO")){
						registraLog("avaliação positiva de mudança de processo");
						return true;
					}
					if(validaFuncao(dummy1)) {
						registraLog("avaliação negativa de mudança de processo");
						return false;
					}
					if(obtemData(dummy1) != null) {
						if(verificaDataFinal(dummy1,x)) {
							registraLog("avaliação positiva de mudança de processo");
							return true;
						}
					}
				}
			}
		} 
		if(validaJuridiques(linhaDummy)) {
			return false;
		}
		registraLog("avaliação negativa de mudança de processo");
		registraLog("Avaliação de mudança de processo iniciado");
		return false;
	}
	
	private static String formataLinhaComProcesso(String linha) {
		String linhaProcesso = linha;
		String dummy = "";
		String linhaDummy = "";
		
		if(linha.startsWith("Processo Nº") || linha.startsWith("PROC. Nº")) {
			dummy = "";
			dummy = obtemNumeroProcesso(linha);
			if(dummy == null) {
				linhaProcesso = linha + carregaLinha(sequencial,true,745);
			}
		}
		
		if(linha.equals("PROC. Nº")) {
			dummy = "";
			linhaDummy = carregaLinha(sequencial,false,745);
			dummy = obtemNumeroProcesso(linhaDummy);
			if(!dummy.isEmpty()) {
				linhaProcesso = linha + " " + linhaDummy;
				sequencial++;
			}
		}
		
		return linhaProcesso;
	}
	
	private static boolean quebraProcesso(int indice) throws Exception {
		String strDummy = "";
		String dummy = "";
		String procDummy = "";
		
		String linhaDummy = formataLinhaComProcesso(carregaLinha(indice, false,1581));

		strDummy = obtemNumeroProcesso(linhaDummy);
		boolean achouFuncao = false;

		int var = linhaDummy.trim().split(" ", -1).length - 1;
		String palavras[] = new String[var];
		String palavra = "";
		registraLog("Verificação de quebra por nº de processo iniciada - " + sequencial);
		if(!validaJuridiques(linhaDummy)) {
			if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
				palavras = linhaDummy.split(" ");
				if(palavras.length != 3) {
					return false;
				}
			}
			dummy = formataPalavra(carregaLinha(sequencial-2,false,1592));			// verifica linha anterior a linha do processo
			if(dummy.equals("poder") || dummy.equals("judiciario") || 
					dummy.equals("poder judiciario")) {
				registraLog("quebra por nº de processo não identificada");
				registraLog("--- >> " + sequencial + " - quebra processo");
				return false;
			}
			palavra = "";
			if(assunto.isEmpty() && (textoEdital.isEmpty() && paragrafos.isEmpty())) {
				registraLog("quebra por nº de processo identificada");
				registraLog("--- >> " + sequencial + " - quebra processo");
				return true;
			}
			
			if(!strDummy.equals(processoNumero)) {
				if(!formataPalavra(assunto).equals("pauta de julgamento")) {						// se assuto ñ for pauta
					if(!validaAssunto(formataPalavra(linhaAnterior)) && !assunto.isEmpty()) {		// linha anterior ñ é um assunto válido
						if(procuraIntimados()) {
							registraLog("quebra por nº de processo identificada");
							registraLog("--- >> " + sequencial + " - quebra processo");
							return true;
						}
					}	
				}
			}
			
			if(validaAssunto(dummy) && dummy.equals(formataPalavra(assunto))) {
				return true;
			}

			if(strDummy != null) {																					// linha tem nº processo
				if(!formataPalavra(linhaAnterior).contains("judiciario") || linhaAnterior.startsWith("Ordem:")) {	// linha anterior ñ é PODER JUDICIARIO
					
					// loop regressivo
					for(int x = indice-1; x>=indice-10; x--) {				// loop regressivo				
						dummy = formataPalavra(carregaLinha(x,false,1627));
						//dataTeste = dummy;
						
						if(dummy.isEmpty()) {
		        			continue;
		        		}
						
						if(dummy.equals(formataPalavra(secao)) && formataPalavra(carregaLinha(x-1,false,1627)).equals("tribunal regional do trabalho")) {
							return true;
						}
						
						if(dummy.length() >= 42 && dummy.substring(0, 13).matches("\\w{4}\\W\\w{4}\\W\\w{2}\\W")){
							registraLog("quebra por nº de processo não identificada - nºs de processos diferentes");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return false;
						}
						
						if(validaFuncao(dummy)) {
							registraLog("quebra por nº de processo identificada - Uma função identificada em linhas anteriores.");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true; 	//achouFuncao = true;
						}
						
						if(dummy.equals("intimado(s)/citado(s):") || dummy.equals("poder judiciario")) { // encontrou o termo "Intimado(s)/Citado(s):"
							registraLog("quebra por nº de processo identificada - Encontrou Intimados ou poder judiciario");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							if(processoDummy.equals(processoNumero)) {
								return false;								// linha com mesmo nº de processo 
							}
							return true; 									
						}
						
						if(validaAtor(dummy)) {													// assunto ao qual o proc pertence
							registraLog("quebra por nº de processo identificada - Encontrou um ator nas linhas anteriores");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true;
						}
						if(validaAssunto(dummy) && x == sequencialAssunto) {					// assunto ao qual o proc pertence
							registraLog("quebra por nº de processo identificada - Encontrado um assuto nas linhas anteriores");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true;
						}
						if(dummy.equals(formataPalavra(grupo)) && x == sequencialGrupo) {		// Grupo ao qual o proc pertence
							registraLog("quebra por nº de processo identificada");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");	
							return true;
						}
						if((obtemNumeroProcesso(dummy) != null) && (x == sequencialProcesso)) { // o nº processo faz parte do texto
							registraLog("quebra por nº de processo não identificada");
							return false;
						}
						if(verificaDataFinal(dummy,x)){											// data final da publicação anterior
							if(strDummy.equals(processoNumero) && !achouFuncao) {
								registraLog("quebra por nº de processo não identificada");
								return false;
							} else {
								registraLog("quebra por nº de processo identificada");
								registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
								return true;
							}
						}
						if(formataPalavra(dummy).equals("intimado(s)/citado(s):") 				// o nº processo faz parte do texto
								&& formataPalavra(assunto).equals("pauta")) {
							registraLog("quebra por nº de processo não identificada");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return false;
						}
						if(verificaSeLinhaTemNumProcesso(dummy)) {
							if(obtemNumeroProcesso(dummy).equals(processoNumero) && x <= indice) {
								return false;
							}
						}
					}
					
//					if(strDummy.equals(processoNumero)) {
//						return false;
//					}
					
					// loop progressivo 
					for(int x = indice; x<=indice+50; x++) { 
						dummy = formataPalavra(carregaLinha(x,false,1694));
						if(dummy.equals("*** marca fim ***")){
							break;
						}
						if(dummy.isEmpty()) {
		        			continue;
		        		}

						palavras = dummy.split(" ");
						palavra = palavras[0].replaceAll(":", "");
						procDummy = obtemNumeroProcesso(dummy);

						//if(procDummy != null) {
						//	if(avaliaMudancaProcesso(x-1)) {
						//														// pode indicar uma falsa quebra
						//	}
						//}
						
						if(validaAtor(palavra)){
							registraLog("quebra por nº de processo identificada");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true;
						}
						
						if(verificaDataValida(obtemData(dummy))) {
							registraLog("quebra por nº de processo identificada");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true;
						}

						if(formataPalavra(dummy).equals("intimado(s)/citado(s):")) {
							registraLog("quebra por nº de processo identificada");
							registraLog("\t" + "--- >> " + sequencial + " - quebra processo");
							return true;
						}
						
						if(x >= limiteGrupo) {
							break;
						}
					}					
				}
			}
		}			// Fim do if do avaliaMudancaProcesso
	//	}
		return false;
	}

	private static boolean quebraAssunto(int indice, int limite) throws Exception {
		//int in = 0;
		//int fm = 0;
		//String dataInvertida = "";
		//String dta = "";
		String dummy = "";
		String dummy1 = "";
		String linhaDummy = formataPalavra(carregaLinha(indice, false,1748).trim());
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		registraLog("Verificação de quebra por assunto iniciada - " + sequencial);
			dummy = carregaLinha(indice+1,false,1753);
			dummy1 = obtemNumeroProcesso(dummy);
			if(dummy1 != null && !validaJuridiques(dummy) && textoEdital.isEmpty()) {
				registraLog("quebra por assunto identificada");
				registraLog("--- >> " + sequencial + " - quebra assunto");
				return true;
			}
	
			if(indice == sequencialGrupo) {																// já valida o 1º assunto do grupo
				registraLog("quebra por assunto identificada");
				registraLog("--- >> " + sequencial + " - quebra assunto");
				return true;
			} else {		
				for(int x=indice; x>=sequencialProcesso; x--) {										// loop regressivo 
					dummy = formataPalavra(carregaLinha(x,false,1767));
					if(dummy.isEmpty()) {
	        			continue;
	        		}
					if(x == sequencialGrupo) {														// atingiu limite superior
						break;
					}
					if(x == sequencialSecao) {
						registraLog("quebra por assunto identificada");
						registraLog("--- >> " + sequencial + " - quebra assunto");
						return true;
					}
					if(verificaDataValida(dummy)) {													// localizou data final da publicação anterior
						if(verificaDataFinal(dummy, x)) {
							registraLog("quebra por assunto identificada");
							registraLog("--- >> " + sequencial + " - quebra assunto");
							return true;
						}
					}
					if(dummy.equals("poder") || 
							dummy.equals("judiciario") || 
							dummy.equals("poder judiciario")){										// provavelmente assunto no meio da 
						registraLog("quebra por assunto não identificada");
						return false;																// publicação em processamento
					}										
					if(dummy.equals("intimado(s)/citado(s):")) {									// idem
						registraLog("quebra por assunto não identificada");
						return false;
					}
					if(obtemNumeroProcesso(dummy) != null) {
						registraLog("quebra por assunto identificada");
						registraLog("--- >> " + sequencial + " - quebra assunto");
						return true;
					}										
				}
						
				for(int x=indice; x<=limite; x++) {													// loop progressivo
					dummy = formataPalavra(carregaLinha(x,false,1804));
					if(dummy.isEmpty()) {
						continue;
	        		}
					if(atores.contains(primeiraPalavra(dummy))){									// encontrou atores	
						registraLog("quebra por assunto identificada");
						registraLog("--- >> " + sequencial + " - quebra assunto");
						return true;
					}
					if(verificaDataValida(dummy)) {													// encontrou data final da proxima publicação
						if(verificaDataFinal(dummy, x)) {
							registraLog("quebra por assunto identificada");
							registraLog("--- >> " + sequencial + " - quebra assunto");
							return true;
						}
					}
					if((obtemNumeroProcesso(dummy) != null) && pauta) {								// encotrou nº processo e assunto = pauta
						return true;
					}
					if(obtemNumeroProcesso(dummy) != null) {
						if(!assunto.isEmpty() && !processoLinha.isEmpty() && !textoEdital.isEmpty() || (indice == (x-1))) {
							registraLog("quebra por assunto identificada");
							registraLog("--- >> " + sequencial + " - quebra assunto");
							return true;
						}
					}
				}						
			}
		registraLog("quebra por assunto não identificada");
		return false;
	}
	
	private static boolean validaAtor(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(tabelaAtores.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(tabelaAtores.contains(dummy)) {
					intDummy = tabelaAtores.indexOf(dummy);
					strDummy = tabelaAtores.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	private static boolean verificaStopWords(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]"," ");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(stopWords.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(stopWords.contains(dummy)) {
					intDummy = stopWords.indexOf(dummy);
					strDummy = stopWords.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	private static boolean validaFuncao(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]","");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.replaceAll("[()ºª/]","");
		linhaDummy = linhaDummy.replaceAll("[&]","");
		linhaDummy = linhaDummy.trim();
		
		int intDummy = 0;
		String strDummy = "";
		String dummy = "";
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		if(funcoes.contains(linhaDummy)) {
			return true;
		} else {
			dummy = palavras[0].trim();
			for(int x=1; x<=palavras.length-1; x++ ) {
				if(funcoes.contains(dummy)) {
					intDummy = funcoes.indexOf(dummy);
					strDummy = funcoes.get(intDummy).trim();
					if(strDummy.length() == dummy.length()){
						return true;
					}
				} else {
					dummy = dummy + " " + palavras[x];
					dummy = dummy.trim();
				}		
			}
		}
		return false;
	}
	
	private static boolean validaJuridiques(String linhaDummy) {
		
		linhaDummy = formataPalavra(linhaDummy);
		linhaDummy = linhaDummy.replaceAll("[,:;-]ºª","");
		linhaDummy = linhaDummy.replaceAll("[0123456789.]","");
		linhaDummy = linhaDummy.replaceAll("[ºª]","");
		linhaDummy = linhaDummy.replaceAll("[-()]","");
		linhaDummy = linhaDummy.replaceAll("[,.]","");
		linhaDummy = linhaDummy.trim();

		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		for(int x=0; x<=palavras.length-1; x++) {
			if(palavras[x].isEmpty()) {
				continue;
			}
			if(juridiques.contains(palavras[x].trim())) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean verificaMeses(String linhaDummy) {

		String dummy = "";
		int var = linhaDummy .split(" ", -1).length - 1;      
		String palavras[] = new String[var];                
		palavras = linhaDummy .split(" ");

		for(int x=0; x<=palavras.length-1; x++ ) {
			dummy = palavras[x].trim();
			if(dummy.isEmpty()) {
				continue;
			} else {
				if(meses.contains(dummy)) {
					return true;
				}
			}	
		}
		return false;
	}

	private static boolean validaAssunto(String linhaAssunto) {
		String linhaDummy = "";
		linhaAssunto = formataPalavra(linhaAssunto).trim();
		int intDummy = 0;
		/*
		int var = linhaAssunto.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		
		linhaAssunto  = linhaAssunto.replaceAll("[01234567890ªº]","*");
		var2 = linhaAssunto.split(" ");
		
		for(int ix=0;ix<=var2.length-1;ix++) {
			if(var2[ix].equals("*") || var2[ix].equals("**")) {
				continue;
			}
			linhaDummy = linhaDummy + " " + var2[ix].trim();
		}
		linhaDummy = linhaDummy.trim();
		*/
		if(tabelaAssuntos.contains(linhaAssunto)) {
			intDummy = tabelaAssuntos.indexOf(linhaAssunto);
			if(tabelaAssuntos.get(intDummy).length() == linhaAssunto.length()){
				return true;
			}
		}
		return false;
	}
	
	private static boolean validaExcecaoAssunto(String linhaAssunto) {				// exceções da tabelaAssuntos
		if(linhaAssunto.startsWith("edital edhpi")) {							
			return true;
		}
		return false;
	}
/*	
	private static int contaKeyWords(String linhaDummy) {
		String dummy = "";
		int contador = 0;

		linhaDummy  = formataPalavra(linhaDummy );	
		linhaDummy  = linhaDummy.replaceAll("[(),;.:-]"," ");
		
		int var = linhaDummy .split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaDummy .split(" ");	
		for(int x=0; x<= var2.length-1; x++) {
			if(var2[x].isEmpty()) {
				continue;
			} else {
				if(keyWords.contains(var2[x].trim())) {
					contador++;
				}
			}
		}
		return contador;
	}
*/	
	private static String carregaLinha(int indice, boolean incrementar, int numLinha) {
		int ix = indice;
		int in = 0;
		String linha = "";
		boolean saida = false;
		String linhaOrigem = completaEsquerda(Integer.toString(numLinha),'0',7);
		while(bufferFormatado.get(ix).trim().equals("") || bufferFormatado.get(ix).trim().isEmpty()) {
			ix++;
		}

		if(bufferFormatado.get(ix).startsWith("Código para aferir autenticidade") ||
				bufferFormatado.get(ix).startsWith("Data da Disponibilização:") ||
				primeiraPalavra(bufferFormatado.get(ix)).matches("\\d{4}\\W\\d{4}")){
			in = ix;

			while(!saida) {
				if(bufferFormatado.get(in).startsWith("Código para aferir autenticidade") ||
						bufferFormatado.get(in).startsWith("Data da Disponibilização:") ||
						primeiraPalavra(bufferFormatado.get(in)).matches("\\d{4}\\W\\d{4}") ||
								(bufferFormatado.get(in).trim().equals("") || bufferFormatado.get(in).trim().isEmpty())){
					if(primeiraPalavra(bufferFormatado.get(ix)).matches("\\d{4}\\W\\d{4}")){ 
						pagina = obtemPagina(bufferFormatado.get(ix));
					}
					in++;
					continue;
				} else {
					ix = in;
					break;
				}
			}
		}

		if(incrementar) {
			if(in > 0) {
				sequencial = in+1;
			} else {
				sequencial++;
			}
		} 
		if(incrementar) {
			registraLog("("+ linhaOrigem + ") linha: " + sequencial + " carregada.");
		} else {
			registraLog("("+ linhaOrigem + ") linha: " + " lida.");
		}
		linha = bufferFormatado.get(ix).trim().replace("&", "e");
		return linha;
	}

	private static int obtemPagina(String linhaDummy) {
		int idx = 0;
		String strPagina = "";
		char digito;
		boolean saida = false;
		String primeiraPalavra = primeiraPalavra(linhaDummy);
		if(primeiraPalavra.matches("\\d{4}\\W\\d{4}")){
			if(numeroEdicao.isEmpty()) numeroEdicao = primeiraPalavra;
			idx = linhaDummy.length()-1;
			while(!saida){
				digito =linhaDummy.charAt(idx);
				if(linhaDummy.charAt(idx) >= '0' && linhaDummy.charAt(idx) <= '9'){						
					if(strPagina.isEmpty()) {
						strPagina = Character.toString(digito);
					} else {
						strPagina = Character.toString(digito) + strPagina;
					}
				} else {
					saida = true;
					break;
				}
				idx--;
			}
		} else {
			strPagina = "0";
		}
		if(strPagina.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(strPagina);
		}
	}
	
	private static String carregaLinhaIndice() {
		int x = 0;
		String linhaDummy  = "";
		
		if(primeiraPalavra(bufferFormatado.get(sequencialIndice)).matches("\\d{4}\\W\\d{4}")){
			for(x = sequencialIndice; x<=bufferFormatado.size()-1;x++) {
				if(!bufferFormatado.get(x).startsWith("Código para aferir autenticidade")){
					continue; 
				} else {
					sequencialIndice = x+1;
					break;
				}
			}
		}
		if(sequencialIndice == bufferFormatado.size()) {
			seqIndex = sequencialIndice;
			sequencialIndice = sequencialIndice-1;
			linhaDummy  = bufferFormatado.get(bufferFormatado.size()-1);
		} else {
			seqIndex = sequencialIndice;
			linhaDummy  = bufferFormatado.get(sequencialIndice);
			sequencialIndice++;
		}
		seqIndex = sequencialIndice;
		return linhaDummy ;
	}
/* aqui
	private static boolean verifcaLetras(String linha) {
		char[] c = linha.toCharArray();
		boolean d = true;
		for ( int i = 0; i < c.length; i++ ) {
		    // verifica se o char não é um dígito
		    if ( !Character.isDigit( c[ i ] ) ) {
		        d = false;
		        break;
		    }
		}
		return d;
	}
*/
	private static boolean verificaFimAtores(int indice) {
		
	//concluir esse método para garantir o fim dos atores
		
		
		String linhaDummy = "";
		String dummy = "";
		for (int x=indice; x<101; x++ ) {
			linhaDummy = formataPalavra(carregaLinha(x, false,2171));
			dummy = formataPalavra(carregaLinha(x+1, false,2172));
			if(linhaDummy.equals("intimado(s)/citado(s):") || 
					linhaDummy.startsWith("poder judiciario") ||
					(linhaDummy.equals("poder") && dummy.equals("judiciario")) ||
					linhaDummy.startsWith("ordem:") ||
					verificaDataValida(linhaDummy) ||
					verificaSeLinhaTemNumProcesso(linhaDummy)){
				break;
			}
		}
		return false;
	}
	
	private static String trataAtores(String linhaDummy) throws Exception {
		
		char pto = ' ';
		boolean saida = false;
		String bloco = "";	
		String registro = "";
		linhaDummy  = limpaCaracteres(linhaDummy );
		if(linhaDummy .contains("&")) {
			linhaDummy  = limpaCaracteres(linhaDummy );
		}
		String dummy = "";
		registraLog("Tratamento dos atores inicializado - " + sequencial);
		while(!saida) {
			linhaDummy  = linhaDummy.replaceAll("[():-]"," ");
			if(tabelaAtores.contains(formataPalavra(primeiraPalavra(linhaDummy )))) {
				registro = formataPalavra(linhaDummy );
				registro = registro.replaceAll("[():-]"," ");
				while(!saida) {
					if(verificaFimAtores(sequencial)) {
						break;
					}
					dummy = formataPalavra(linhaDummy );
					if(dummy.equals("intimado(s)/citado(s):") || 
							dummy.equals("poder") ||
							dummy.equals("judiciario") ||
							dummy.startsWith("ordem:") ||
							verificaDataValida(linhaDummy) || dummy.startsWith("d e s p a c h o") || dummy.startsWith("despacho") ||
							verificaSeLinhaTemNumProcesso(linhaDummy)){
						if(dummy.equals("intimado(s)/citado(s):") || dummy.startsWith("processo:") || dummy.startsWith("processo n") ||
								dummy.startsWith("d e s p a c h o")){
							sequencial--;
						}
						saida = true;
						break;
					}
					if(assunto.contains("Edital EDHPI-") && bloco.contains("Executado")) {
						saida = true;
						break;
					} else {
						if(!linhaDummy .contains("-----")) {
							if(validaAtor(registro)) {
								if(bloco.isEmpty()) {
									bloco = linhaDummy ;
								} else {
									bloco = bloco + "\n" + linhaDummy.trim();
								}
							} else {				
								if(pto == '.' || pto == ')') {
									bloco = bloco + "\n" + linhaDummy.trim();
								} else {
									bloco = bloco + " " + linhaDummy.trim();
								}
							}
						}
					}
					pto = linhaDummy .charAt(linhaDummy .length()-1);
					linhaDummy  = carregaLinha(sequencial,true,2240);
					linhaDummy  = linhaDummy .replaceAll("&[():-]"," ");
					registro = formataPalavra(linhaDummy );
					registro = registro.replaceAll("[():-]"," ");
		    	}
			}

			if(saida) {
				break;
			} else {
				linhaDummy = carregaLinha(sequencial,true,2250);
			}
		}	
	
		registraLog("Tratamento de atores finalizado - " + sequencial);
		//sequencial = sequencial - 2;
		
		if(bloco.length() > 700) {
			registraLog("verificar atores");
		}
		return bloco;
	}

/* aqui
	private static int proximaSequencial(int sequencia) {
		int in = sequencia;
		if(!bufferEntrada.get(in+1).startsWith("Código para aferir autenticidade") &&
				!bufferEntrada.get(in+1).startsWith("Data da Disponibilização:") &&
				!primeiraPalavra(bufferEntrada.get(in+1)).matches("\\d{4}\\W\\d{4}")) {
			return in+1;
		} else {
			while(in <= sequencia+4) {
				if(bufferEntrada.get(in).startsWith("Código para aferir autenticidade") ||
						bufferEntrada.get(in).startsWith("Data da Disponibilização:") ||
						primeiraPalavra(bufferEntrada.get(in)).matches("\\d{4}\\W\\d{4}")){				
					in++;
					continue;
				} else {
					break;
				}
			}
		}
		return in;
	}
*/
/*	Aqui
	private static boolean verificaPalavraChave(String linhaAtual) {
		int igualidades = 0;
		int var = linhaAtual.split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaAtual.split(" ");
		
		for(int i = 0; i <= var; i++){
		    if(stopWords.contains(var2[i])) {
		    	igualidades++;
		    }
		}

		if(igualidades >= 5) {
			return true;
		} else {
			return false;
		}
	}
*/
/* aqui
	private static double contaJuridiques(String linhaDummy) {
		double resultado = 0.0;
		double acertos = 0.0;
		int var = linhaDummy.split(" ", -1).length - 1;
		String palavras[] = new String[var];
		palavras = linhaDummy.trim().split(" ");
		for(int x=0; x <= palavras.length-1; x++) {
			if(juridiques.contains(formataPalavra(palavras[x]))) {
				acertos++;
			}
		}
		resultado = (acertos / palavras.length);
		return resultado;
	}
*/

/*	Este método faz parte do desenvolvimento da versão com formatação de texto
	private static boolean verificaSeEhParagrafo() {
		String linhaDummy = "";
		String dummy = "";
		int inx = sequencial-1;
		boolean ehMaiusculo = false;

		for(int x = inx; x <= inx + 50; x++){
			linhaDummy = carregaLinha(x,false);
			if(linhaDummy.equals("Intimado(s)/Citado(s):")) {
				break;
			}
			if(linhaDummy.charAt(0) >= 'A' && linhaDummy.charAt(0) <= 'Z' && !ehMaiusculo) {
				if(linhaDummy.charAt(linhaDummy.length()-1) == '.' && contaJuridiques(linhaDummy) > 2) {
					return true;
				}
				ehMaiusculo = true;
			} else {
				if (ehMaiusculo){
					if(contaPalavras(linhaDummy) <= 3 && linhaDummy.charAt(linhaDummy.length()-1) == '.') {
						continue;
					}
					if(linhaDummy.charAt(linhaDummy.length()-1) == '.') {
						if(contaJuridiques(dummy + " " + linhaDummy) > 2) {
							return true;
						}
					}	
					if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
						break;
					}
					if(verificaDataValida(linhaDummy)) {
						break;
					}
					if(validaAssunto(linhaDummy)) {
						break;
					}
					if(x == limiteGrupo) {
						break;
					}
				}
				dummy = dummy + linhaDummy;
			}	
			
		}
		return false;
	}
*/

	private static String trataIntimados(String linhaDummy) throws IOException {
		registraLog("Inídio do tratamento dos intimados");
		//String linhaDummy = carregaLinha(sequencial,true);
		if(linhaDummy.contains("&")) {
			linhaDummy = limpaCaracteres(linhaDummy);
		}
		String bloco = "";
		String dummy = "";
		String linhaDummyAnterior = "";
		//int ix = 0;
		boolean saida = false;
		//boolean exit = false;
		String var2[]; 
		var2 = linhaDummy.split(" ");
		dummy = formataPalavra(linhaDummy);

		if(dummy.equals("intimado(s)/citado(s):") && 
				!assunto.contains("Edital EDHPI-") && 
				!verificaSeLinhaTemNumProcesso(linhaDummy))
			{
				while(!saida) {	
					if(bloco.isEmpty()) {									// guarda intimados
						bloco = linhaDummy;
						linhaDummy = carregaLinha(sequencial,true,2393);
						if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
							saida = true;
							break;
						}
						if(linhaDummy.contains("&")) {
							linhaDummy = limpaCaracteres(linhaDummy);
						}
						continue;
					} else {
						if(linhaDummy.charAt(0) == '-') {
							bloco = bloco + "\n" + linhaDummy;
							linhaDummyAnterior = linhaDummy;
							linhaDummy = carregaLinha(sequencial,true,2406);
							if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
								saida = true;
								break;
							}
							if(linhaDummy.contains("&")) {
								linhaDummy = limpaCaracteres(linhaDummy);
							}
							continue;
						} else {
							if(!juridiques.contains(formataPalavra(linhaDummy))) {
								if(!validaParagrafo(sequencial)) {
								//	if(temContinuacao(linhaDummyAnterior)) {
										if(linhaDummy.charAt(0) != '-' && verificaMaiuscula(linhaDummy) && verificaMaiuscula(linhaDummyAnterior)) {
											if(bloco.length() < 180 && (bloco.length() - linhaDummy.length() > linhaDummy.length() )) {
												bloco = bloco + " " + linhaDummy;
											} else {
												bloco = bloco + "\n" + linhaDummy;
											}
											linhaDummyAnterior = linhaDummy;
											linhaDummy = carregaLinha(sequencial,true,2426);
											if(verificaSeLinhaTemNumProcesso(linhaDummy)) {
												saida = true;
												break;
											}
											if(linhaDummy.contains("&")) {
												linhaDummy = limpaCaracteres(linhaDummy);
											}
											continue;
										}
								//	}
								}
							}					
							saida = true;
							break;
						}	
					}
				}
			}
		if(!bloco.isEmpty()) {
			bloco = bloco + "\n";
		}
		registraLog("Fim do tratamento dos intimados");
		sequencial--;											// ajuste para no retorno continua na próxima linha após os intimados
		return bloco;
	}
		
	private static int contaPalavras(String linhaDummy) {
		int ix = 0;
		int var = linhaDummy.split(" ", -1).length - 1;      	//pega a quantidade de espaços em branco
		String var2[] = new String[var];                		//define o vetor que conterá as palavras separadas da string
		var2 = linhaDummy.split(" ");                        	//separa a string colocando as palavras no vetor
		for(int i = 0; i <= var; i++){
		    ix++;
		}
		return ix;
	}
	
/*	aqui
	private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
*/
	private static boolean verificaSemelhanca(String comparado, String comparador) {

		int palavrasIguais = 0;
		int letrasIguais = 0;
		int esp1 = comparado.split(" ", -1).length - 1; 
		int esp2 = comparador.split(" ", -1).length - 1;
		int limite = 0;
		int edge = 0;
		double percent = 0.f;

		String plvrsCmprdo[] = new String[esp1];  
		String plvrsCmprdr[] = new String[esp2];

		plvrsCmprdo = comparado.split(" ");
		plvrsCmprdr = comparador.split(" ");
		
		if(plvrsCmprdo.length <= plvrsCmprdr.length) {
			limite = plvrsCmprdo.length-1;
		} else {
			limite = plvrsCmprdr.length-1;
		}

		for(int ix=0; ix<=limite; ix++) {
			if(plvrsCmprdo[ix].equals(plvrsCmprdr[ix])) {
				palavrasIguais++;										
			} else {
				if(plvrsCmprdo[ix].length() <= plvrsCmprdr[ix].length()) {
					edge = plvrsCmprdo[ix].length()-1;
				} else {
					edge = plvrsCmprdr[ix].length()-1;
				}
				for(int il=0; il<=edge; il++) {
					if(plvrsCmprdo[ix].charAt(il) == plvrsCmprdr[ix].charAt(il)){
						letrasIguais++;
					}
				}
				percent = (double)letrasIguais/plvrsCmprdr[ix].length();
				if(letrasIguais > 7.0) {
					palavrasIguais++;
				}
			}
		}
		
		if(palavrasIguais > 0) {
			if(palavrasIguais == comparado.length()) {
				return true;
			}
			if((palavrasIguais / comparador.length()) < 3) {	// esse teste tá errado
				return true;
			}
		}
		return false;
	}
	
	private static String trataNumEdicao(String linhaDummy) {
		String[] gabaritos = new String[3];
		gabaritos[0] = "\\d{4}\\W\\d{4}";
		gabaritos[1] = "\\d{5}\\W\\d{4}";
		gabaritos[2] = "\\d{6}\\W\\d{4}";
		int var = linhaDummy.split(" ", -1).length - 1;      
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");                        
		if (!var2[0].equals("")){
			for (int inx = 0; inx <= 2; inx++){
				if(var2[0].matches(gabaritos[inx])){
					return var2[0];
				}					
			}		
		}
		return null;
	}
		
	private static String ultimaPalavra(String linhaDummy) {
		//int ix = 0;
		int var = linhaDummy.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");   
		return var2[var2.length-1];
	}
	
	private static String primeiraPalavra(String linhaDummy) {
		int var = linhaDummy.split(" ", -1).length - 1;
		String var2[] = new String[var];                
		var2 = linhaDummy.split(" ");   
		if(var2.length == 0) {
			return " ";
		}
		return var2[0];
	}

	private static void finalizaProcesso() throws IOException {
		
		if(tipoProcessamento.equals("DESKTOP")){
			Comuns.apresentaMenssagem("Fim do processamento, incluidos " + totalPublicacoes + "no servidor", tipoProcessamento, "informativo",null);
			Comuns.apresentaMenssagem("Fim do Processamento", tipoProcessamento, "final",null);
		} else if(tipoProcessamento.equals("BATCH")) {
			Comuns.apresentaMenssagem("\"Criados \" + totalPublicacoes + \"publicacoes\"", tipoProcessamento, "informativa", null);
			Comuns.apresentaMenssagem("Fim do Processamento", tipoProcessamento, "informativa", null);
		}
		gravaIndiceXML();
        System.exit(0);
	}
		
	private static boolean conectaServidor() throws IOException {
		registraLog("Conexão com o servidor.\n");
		Comuns.apresentaMenssagem("Conexão com o servidor.", tipoProcessamento, "informativa", null);
		conexao.setUser(usuario);
		conexao.setPassword(password);
		conexao.setUrl(url);
		sessao = InterfaceServidor.serverConnect();
		if (sessao == null) {
			registraLog("Falha na conexao com o servidor");
			return false;
		}
		registraLog("Conexão com o servidor concluída.");
		return true;
	}

	private static boolean carregaEdicao(File input) throws Exception{
		boolean retorno = false;
		int numPaginas = 0;
		registraLog("Início da carga do PDF da edição (carregaEdicao)");
	    String texto = "";
		Comuns.apresentaMenssagem("Carregando edição.", tipoProcessamento, "informativa", null);
		try {
			PDDocument pd = PDDocument.load(input);
			numPaginas = pd.getNumberOfPages();
			if(numPaginas >= 2) {
	        	PDFTextStripper stripper = new PDFTextStripper();	       
	        	texto = stripper.getText(pd);
	        	separaLinhas(texto);
	        	Comuns.apresentaMenssagem("Fim do carregamento da edição.", tipoProcessamento, "informativa", null);	
	    		registraLog("Fim da carga do PDF da edição (carregaEdicao)");
	        	retorno = true;
			} else {
				Comuns.apresentaMenssagem("Edição sem publicações.", tipoProcessamento, "informativa", null);	
	    		registraLog("(carregaEdicao) Edição sem publicações - nº de paginas: " + numPaginas);
			}
	        pd.close();
	    }
	    catch (IOException erro) {
	    	Comuns.apresentaMenssagem("Erro no carregamento do Diário Oficial  -> ", tipoProcessamento, "erro",  erro.toString());
	    }
		return retorno;
	}
	
	private static void separaLinhas(String texto) {
		registraLog("Início da carga da edição no bufferFormatado (separaLinhas)");
		String result = "";
		int endIndex = 0 ;
		result = texto.replaceAll("\\n", "%%");
		result = result.replaceAll("\\r", "");
		int beginIndex = result.indexOf("%%");

		for(int i = 0; i <= result.length()-1; i++) {
			endIndex = result.indexOf("%%", beginIndex+2);
			if(endIndex < 0) break;
			bufferEntrada.add(result.substring(beginIndex+2, endIndex));
			beginIndex = endIndex;
		}
		bufferEntrada.add("*** MARCA FIM ***");
		registraLog("Fim da carga da edicao no bufferEntrada (separaLinhas)");
	}
		
	private static boolean contemNumeros(String str) {
		
		int numDigitos = 0;
        if (str == null || str.length() == 0) return false; 
        for (int i = 0; i <= str.length()-1; i++) {
            if (Character.isDigit(str.charAt(i)))
                numDigitos++;
        }
        if(numDigitos >= 5) {
        	return true;
        }
        return false;
    }
		
	private static String obtemNumeroProcesso(String linhaDummy){				

		String sequencia = "";
		String[] gabaritos = new String[17];

		gabaritos[0] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[1] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{3}";
		gabaritos[2] = "\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[3] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{3}\\W\\d{3}";
		gabaritos[4] = "\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[5] = "\\d\\W\\d{5}\\W\\d\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[6] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\{2}\\W\\d{4}";
		gabaritos[7] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\W{2}\\d{5}\\W\\d{4}\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d\\W}";
		gabaritos[8] = "\\w{7}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[9] = "\\d{3}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[10] = "\\d{5}\\W\\d\\W\\d{3}\\W\\d{2}\\W\\d{2}\\W\\d";
		gabaritos[11] = "\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\W";
		gabaritos[12] = "\\w{8}\\s\\w\\W\\s\\w{3}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}";
		gabaritos[13] = "\\w{8}\\s\\w\\W\\s\\w{3}\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\w{4}\\W";
		gabaritos[14] = "\\w{6}\\s\\{2}w\\s\\w{8}\\W\\s\\W\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\s\\w{3}";
		gabaritos[15] = "\\w{8}\\s\\w\\W\\s\\w{3}\\s\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{4}\\s\\W\\d{2}\\W";
		gabaritos[16] = "\\w{4}\\W\\s\\w\\W\\s\\w{3}\\s\\W\\s\\d{7}\\W\\d{2}\\W\\d{4}\\W\\d\\W\\d{2}\\W\\d{3}";
		
		if(!contemNumeros(linhaDummy)) {
			return null;
		}
				
		linhaDummy = linhaDummy.replace(" ", ".");
	
		for (int ix = 0; ix <= linhaDummy.length()-1; ix++) {
			if ((linhaDummy.charAt(ix) >= '0' && linhaDummy.charAt(ix) <= '9') || 
					(linhaDummy.charAt(ix) == '.' || 
					linhaDummy.charAt(ix) == '-' || 
					linhaDummy.charAt(ix) == '/' || 
					linhaDummy.charAt(ix) == ',')){
				
					if ((linhaDummy.charAt(ix) >= '0' && linhaDummy.charAt(ix) <= '9')||
						((linhaDummy.charAt(ix) == '.' || 
						linhaDummy.charAt(ix) == '/' || 
						linhaDummy.charAt(ix) == ',' || 
						linhaDummy.charAt(ix) == '-')) && sequencia.length() > 0) {						
								sequencia = sequencia + linhaDummy.charAt(ix);
					}		 
			}
		}
				
		if(sequencia.length() >= 26) {
			sequencia = sequencia.substring(0, 25);
		}

		if (!sequencia.equals("")){
			for (int inx = 0; inx <= 16; inx++){
				if(sequencia.matches(gabaritos[inx])){
					return sequencia;
				}					
			}		
		}
		return null;
	} 			// fim do metodo extraiprocessoLido
		
//	private static void inicializaArquivos() throws IOException{	
//		
//		//JFileChooser arquivo = new JFileChooser();
//		arquivo.showDialog(null, "Selecionar um Arquivo");
//      File diario = arquivo.getSelectedFile();
//
//        if (diario != null){
//        //	logFolder = new File(diario.getParentFile()+logFolder);
//    		intermedio = new File(diario.getParentFile()+"/intermedio.txt");
//    		assuntos = new File(diario.getParentFile()+"/subjects.txt");
//    	//	config = new File(diario.getParentFile()+"/split.cnf");			// ñ é usado
//        	diarioInput = diario;
//        }
//	}																		// Fim do método InicializaArquivos
	
	private static String limpaCaracteres(String linhaDummy) { 
		linhaDummy = linhaDummy.replaceAll("&","e");
		return linhaDummy;
	}
		
	private static String formataPalavra(String palavra) {    			// retira acentos e transforma para minusculas
        
        String palavraFormatada = "";
		if(palavra != null){
			palavra = palavra.replaceAll("[aáàãâäåAÁÀÃÂÄÅ]","a");
	        palavra = palavra.replaceAll("[eéèêëEÉÈÊË]","e");
	        palavra = palavra.replaceAll("[iíìîïIÍÌÎÏ]","i");
	        palavra = palavra.replaceAll("[oóòôöOÓÒÔÖÕ]","o");
	        palavra = palavra.replaceAll("[uúùûüUÚÙÛÜ]","u");
	        palavra = palavra.replaceAll("[çÇ]","c");
	        palavra = palavra.replaceAll("[B]","b");
	        palavra = palavra.replaceAll("[C]","c");
	        palavra = palavra.replaceAll("[D]","d");
	        palavra = palavra.replaceAll("[F]","f");
	        palavra = palavra.replaceAll("[G]","g");
	        palavra = palavra.replaceAll("[H]","h");
	        palavra = palavra.replaceAll("[J]","j");
	        palavra = palavra.replaceAll("[L]","l");
	        palavra = palavra.replaceAll("[M]","m");
	        palavra = palavra.replaceAll("[N]","n");
	        palavra = palavra.replaceAll("[P]","p");
	        palavra = palavra.replaceAll("[R]","r");
	        palavra = palavra.replaceAll("[S]","s");
	        palavra = palavra.replaceAll("[T]","t");
	        palavra = palavra.replaceAll("[Q]","q");
	        palavra = palavra.replaceAll("[V]","v");
	        palavra = palavra.replaceAll("[X]","x");
	        palavra = palavra.replaceAll("[Z]","z");
	        palavra = palavra.replaceAll("[W]","w");
	        palavra = palavra.replaceAll("[Y]","y");
	        palavra = palavra.replaceAll("[K]","k");

	        for(int i=0; i <= palavra.length()-1; i++) {
	        	if ((palavra.charAt(i) == ' ' && (i > 0 && palavra.charAt(i-1) == ' '))){
	        		continue;
	        	} else {
	        		palavraFormatada = palavraFormatada + palavra.charAt(i);
	        	}        	
	        } 
        }
    return palavraFormatada;  
    }
	
	private static int posicionaIndice() { 
		registraLog("Posicionando início do indice");
		int ultimaLinha = bufferFormatado.size()-1;

		for(int x=0; x<=2000; x++) {
			if(bufferFormatado.get(ultimaLinha).equals("SUMÁRIO")) {
				registraLog("Início do indice localizado");
				return ultimaLinha;
			}
			ultimaLinha--;
		}
		registraLog("Início do indice não localizado");
		return -1;
		
	}

	private static void carregaIndice() throws Exception {
		registraLog("Início do carregamento do indíce da edição");
		String grupo = "";
		String strPagina = "";
		String paginaSecao = "";
		String secao = "";
		String dummy = "";
		String paginaGrupo = "";
		String linha = "";
		String rabo = "";
		String complemento = "complemento";
		int indexSecao = 0;
		int indexGrupo = 0;
		int seq = 0;
		boolean continua = false;
		boolean ehGrupo = false;

		//sequencialIndice = bufferEntrada.indexOf("SUMÁRIO")+1;
		sequencialIndice = posicionaIndice()+1;
		bufferFormatado.remove(sequencialIndice-1);
		sequencialIndice--;
		Index.clear();
		Comuns.apresentaMenssagem("Início do carregamento do índice desta edição.", tipoProcessamento, "informativa", null);
		try {
		
			if(sequencialIndice > 0) {
				while(sequencialIndice <= bufferFormatado.size()-1) {	
					linha = carregaLinhaIndice();
					seq = seqIndex;	
					if(linha.charAt(0) == ' ')  {
						ehGrupo = true;
					}	
					rabo = ultimaPalavra(linha).trim();
					linha = linha.trim();
					if(linha.equals("*** MARCA FIM ***")) {
						ultimaLinha = sequencialIndice;
						break;
					}
					
					if(!continua) {
						if(ehInteiro(rabo) && contaPalavras(linha) > 1) {
							strPagina = rabo;
							dummy = linha.substring(0, linha.length()-strPagina.length());
							bufferFormatado.remove(sequencialIndice-1);
							sequencialIndice--;
						} 
	
						if(dummy.isEmpty()) {
							dummy = linha;
							bufferFormatado.remove(sequencialIndice-1);
							sequencialIndice--;
							if(!ehInteiro(rabo)) {
								continua = true;
							} 
						} 
					} else {
						dummy = dummy + " " + linha;
						continua = false;
						bufferFormatado.remove(sequencialIndice-1);
						sequencialIndice--;
						continue;
					}
	
					if(ehInteiro(rabo) && contaPalavras(linha) == 1) {
						strPagina = linha.trim();
						bufferFormatado.remove(sequencialIndice-1);
						sequencialIndice--;
					}
	
					if(ehInteiro(rabo)) {
						if(!ehGrupo){
							secao = dummy.trim();																													
							paginaSecao = strPagina;
							indexSecao = seqIndex;
							grupo = "";
							dummy = "";
						} else {
							grupo = dummy.trim();
							indexGrupo = seqIndex;
							paginaGrupo = rabo;
							IndiceEdicao regIndice = new IndiceEdicao(secao, 
																		Integer.parseInt(paginaSecao), 
																		0, 
																		complemento, 
																		grupo, 
																		Integer.parseInt(paginaGrupo), 
																		0, 
																		indexSecao, 
																		indexGrupo);
							Index.add(regIndice);
							ultimaPagina = Integer.parseInt(paginaGrupo);
							strPagina = "";
							dummy = "";
							ehGrupo = false;
						}
					}
				}	// fim do while
			} else {
				Comuns.apresentaMenssagem("Índice do Diário Oficial não localizado ", tipoProcessamento, "erro",  null);
				finalizaProcesso();
			}
		}  catch (Exception e) {
			Comuns.apresentaMenssagem("Erro na indexação: " + e.toString(), tipoProcessamento, "erro",  null);
            e.printStackTrace();
        }
		Comuns.apresentaMenssagem("Fim do carregamento do índice desta edição.", tipoProcessamento, "informativa", null);
		registraLog("Fim do carregamento do indíce da edição");
	}							
		
	private static boolean ehInteiro( String linhaDummy ) {		    
	    char[] c = linhaDummy.toCharArray();
	    boolean d = true;		    
	    if(linhaDummy.equals("") || linhaDummy.equals(" ")){
	    	return false;
	    }		    
	    for ( int i = 0; i < c.length; i++ ){		        
	        if (!Character.isDigit(c[ i ])) {
	            d = false;
	            break;
	        }
	    }
	    return d;
	}
	
	private static String obtemEdicao(String linhaDummy){			

		int i = 0;
		int ln = 0;
		int numero, alfa, conv, x, ix;
		String[] grupos = new String[160];
		ArrayList<String> meses = new ArrayList<String>();
		char[] digitos;
		String dia = ""; 
		String mes = ""; 
		String ano = "";
		String argumento;
		
		grupos[ln] = "";
		meses.add("janeiro"); 
		meses.add("fevereiro");
		meses.add("marco");
		meses.add("abril");
		meses.add("maio");
		meses.add("junho");
		meses.add("julho");
		meses.add("agosto");
		meses.add("setembro");
		meses.add("outubro");
		meses.add("novembro");
		meses.add("dezembro");
		
		// decomposição da grupos em palavras/numeros
		while (i <= linhaDummy.length()-1) {	
			if((linhaDummy.charAt(i) >= 'A' && linhaDummy.charAt(i) <= 'Z') || 
					(linhaDummy.charAt(i) >= 'a' && linhaDummy.charAt(i) <= 'z') ||
					(linhaDummy.charAt(i) == 'á' || linhaDummy.charAt(i) == 'é' ||
					linhaDummy.charAt(i) == 'í' || linhaDummy.charAt(i) == 'ó' ||
					linhaDummy.charAt(i) == 'ú' || linhaDummy.charAt(i) == 'ã' ||
					linhaDummy.charAt(i) == 'õ' || linhaDummy.charAt(i) == 'ç' ||
					linhaDummy.charAt(i) == 'Á' || linhaDummy.charAt(i) == 'É' ||
					linhaDummy.charAt(i) == 'Í' || linhaDummy.charAt(i) == 'Ó' ||
					linhaDummy.charAt(i) == 'Ú' || linhaDummy.charAt(i) == 'Ã' ||
					linhaDummy.charAt(i) == 'Õ' || linhaDummy.charAt(i) == 'Ç')) {					
						grupos[ln] = grupos[ln] + linhaDummy.charAt(i);					
				}
			
			if((linhaDummy.charAt(i) >= '0' && linhaDummy.charAt(i) <= '9') || (linhaDummy.charAt(i) == '/')){
				grupos[ln] = grupos[ln] + linhaDummy.charAt(i);
			}			
			if (linhaDummy.charAt(i) == ' ' || linhaDummy.charAt(i) == ','){
				ln++;
				grupos[ln] ="";
			}
			i++;
		}
		
		// análise dos grupos decompostos
		alfa = 0;
		numero = 0;
		for(ix = 0; ix <= ln; ix++){								// verifica grupos iniciais
			argumento = formataPalavra(grupos[ix]);
			digitos = argumento.toCharArray();
			for (x = 0; x <= argumento.length()-1; x++){
				if(!Character.isDigit(digitos[x])){
					alfa++;
				} else {
					numero++;
				}
			}
			
			if(alfa >0 && numero == 0){								// verifica se o grupo é um mês
				if(alfa >= 4 && alfa <= 9){
					if(meses.contains(argumento)){

						switch(argumento) {
						case "janeiro":
							mes = "01";	
							break;
						case "fevereiro":
							mes = "02";	
							break;
						case "marco":
							mes = "03";
							break;
						case "abril":
							mes = "04";
							break;
						case "maio": 
							mes = "05";
							break;
						case "junho":
							mes = "06";
							break;
						case "julho":
							mes = "07";
							break;
						case "agosto":
							mes = "08";
							break;
						case "setembro":
							mes = "09";
							break;
						case "outubro":
							mes = "10";
							break;
						case "novembro":
							mes = "11";
							break;
						case "dezembro":
							mes = "12";
							break;
						default:
							mes = "01";
						}
					}					
				}
			}
		
			if((numero == 2 || numero == 1) && alfa == 0){							// verifica se o grupo é um dia
				conv = Integer.parseInt(argumento);
				if (conv >= 1 && conv <= 31){
					dia = argumento;
				}
			}
			
			if((numero == 4 || numero == 2) && alfa == 0){							// verifica se o grupo é um ano
				ano = argumento;
			}
			
		alfa = 0;
		numero = 0;
		}
		dataEdicao = dia + "-" + mes + "-" + ano;
		return ano + "-" + mes + "-" + dia;
	}
	
/*	
	private static boolean verificaDataValida(String linha){

		String linhaData = linha;
		String registro = formataPalavra(linhaData);
		registro = registro.replaceAll("/","");

		if(registro.contains("recifepe")) {
			linhaData = registro.replaceAll("recifepe","recife");
		}
		
		int var = linhaData.split(" ", -1).length - 1;
		String linhaDecomposta[] = new String[var];
		ArrayList<String> uf = new ArrayList<String>();

		uf.add("rio de janeiro");
		uf.add("sao paulo");
		uf.add("belo horizonte");
		uf.add("salvador");
		uf.add("rio de janeiro");
		uf.add("recife");
		uf.add("fortaleza");
		uf.add("belem");
		uf.add("curitiba");
		uf.add("brasilia");
		uf.add("manaus");
		uf.add("florianopolis");
		uf.add("porto velho");
		uf.add("campinas");
		uf.add("sao luiz");
		uf.add("vitoria");
		uf.add("goiania");
		uf.add("aracaju");
		uf.add("natal");
		uf.add("teresina");
		uf.add("cuiaba");
		uf.add("campo grande");
		uf.add("barreiros");

		if(linhaData.charAt(linhaData.length()-1) == '.') {
			linhaData = linhaData.substring(0, linhaData.length()-1);
		}

		linhaDecomposta = linhaData.split(" ");
	
		//if(ehInteiro(linhaDecomposta[0]) && linhaDecomposta[0].length() == 2) {							// DD/MM/AAAA
		//	if(linhaData.matches("\\d{2}\\W\\d{2}\\W\\d{4}") && linhaData.length() == 10){
		//		return true;
		//	}
		//} 

		//if(!ehInteiro(linhaDecomposta[0]) && ehInteiro(linhaDecomposta[linhaDecomposta.length-1])) {	// extenso			
		//	if((linhaDecomposta[0].charAt(linhaDecomposta[0].length()-1) == ',') && 
		//			uf.contains(formataPalavra(linhaDecomposta[0].substring(0, linhaDecomposta[0].length()-1)))) {
		//		return true;
		//	}
		//} 

		if((((ehInteiro(linhaDecomposta[0]) && linhaDecomposta[0].length() == 2)) && (linhaData.matches("\\d{2}\\W\\d{2}\\W\\d{4}") && linhaData.length() == 10)) ||
				((!ehInteiro(linhaDecomposta[0]) && ehInteiro(linhaDecomposta[linhaDecomposta.length-1])) && 
				((linhaDecomposta[0].charAt(linhaDecomposta[0].length()-1) == ',') && uf.contains(formataPalavra(linhaDecomposta[0].substring(0, linhaDecomposta[0].length()-1))))))
			{
				return true;
		} 

		return false;
	}
*/
	private static boolean verificaDataFinal(String linhaComData, int sequencialReferencia){
		
		/**
		 * Verfica se a data lida é a data_final da publicação
		 */
		sequencialReferencia++;
		String linhaDummy = "";
		boolean encontrouAssunto = false;
		boolean encontrouProcesso = false;
		boolean encontrouFuncao = false;
		boolean encontrouGrupo = false;
		int proximoGrupo = localizaProximoGrupo(sequencialReferencia);
		
		if(verificaDataValida(linhaComData)) {	
			for(int x=sequencialReferencia; x<=sequencialReferencia+12; x++) {			// localiza proximo grupo
				linhaDummy = carregaLinha(x, false,3105).trim();
				if(validaFuncao(linhaDummy) && !encontrouFuncao) {
					encontrouFuncao = true;
				}
				if(validaAssunto(linhaDummy) && !encontrouAssunto) {
					encontrouAssunto = true;
				}
				if(verificaSeLinhaTemNumProcesso(linhaDummy) && !encontrouProcesso) {
					if(!obtemNumeroProcesso(linhaDummy).equals(processoNumero)) {
					//	if(encontrouFuncao || encontrouAssunto) {						// avaliar se é uma regra valida
							encontrouProcesso = true;									// pode encontrar processo sem antes encontrar assunto ou funcao?
					//	}																// 
					}
				}
				if(x == proximoGrupo) {													// atigiu o limite antes das 12 linhas
					break;
				}
			}

			if(encontrouFuncao) {
				return true;
			} else if(encontrouAssunto || encontrouProcesso) {
				return true;
			} else if(encontrouGrupo) {
				return true;
			}
		}		
		return false;
	}
		
	private static String completaEsquerda(String value, char c, int size) {
		String result = value;
		while (result.length() < size) {
			result = c + result;
		}
		return result;
	}
		
	private static int atualizaPagina(String argumento){
		
		int posicao = argumento.length()-1;
		String numeroPagina = "";
		
		while(posicao >= 0){

			if ((argumento.charAt(posicao) >= 'a' && argumento.charAt(posicao) <= 'z') || (argumento.charAt(posicao) >= 'A' && argumento.charAt(posicao) <= 'Z')){
				break;
			} else {
				if(argumento.charAt(posicao) >= '0' && argumento.charAt(posicao) <= '9'){	// a linha do indice tem nº da pagina
					numeroPagina = argumento.charAt(posicao) + numeroPagina;
				}
			}
			posicao--;
		}
		if(numeroPagina.isEmpty()) {
			return -1;
		} else {
			return Integer.parseInt(numeroPagina);
		}		
	}

	private static int localizaIndice(String sec, String cmp, String grp, int pgsc, int pggr){

		for(int i = 0; i < Index.size(); i++){
			if(Index.get(i).secao.equals(sec) && Index.get(i).complementoSecao.equals(cmp) && Index.get(i).grupo.equals(grp)){
				if(Index.get(i).paginaSecao == pgsc && Index.get(i).paginaGrupo == pggr) {					
					return i;
				}
			}
		}
		return -1;
	}
		
	private static void mapeiaLinhas() throws IOException{		// mapeia o numero de linha de cada Secao e cada Grupo
		registraLog("Início do mapeamento de linhas.");
		int pagina = 0;
		int sequencia = 0;
		int linhaSecao = 0;
		int regIndice = 0;
		int contador = 0;
		int linhasLidas = 0;
		int intDummy = 0;
		boolean saida = false;
		String linha = "";
		String secaoAnterior = " ";
		String linhaDummy = "";
		String [] arrayLinha;
		Comuns.apresentaMenssagem("Início do mapeamento de linhas.", tipoProcessamento, "informativa", null);
        for (IndiceEdicao Indice : Index) {								// loop do Index
        	if(!secaoAnterior.equals(Indice.secao)){
        		secaoAnterior = "";
        	}							
        	while(sequencia <= bufferFormatado.size()-1){					// 	Loop de procura pela SECAO

    	        linha = bufferFormatado.get(sequencia);
		        sequencia++;
		        if(linha == null) {
		        	break;
		        }

		        if(linha.contains("DEJT Nacional")){
	        		pagina = 1;
	        	}
		        
		        arrayLinha = linha.split(" ");

		        if(linha.contains("Tribunal Regional do Trabalho da") && (linha.substring(0, 18).matches("\\d{4}\\W\\d{4}\\s\\w{8}"))){
		        	intDummy = atualizaPagina(linha);
		        	if(intDummy != -1) {
		        		pagina = intDummy;
		        	}		        										
		        }

	        	if((Indice.secao.trim().equals(linha.trim()) && Indice.paginaSecao == pagina && secaoAnterior.equals(""))){
	        		if(linha.trim().length() == Indice.secao.length()){
	        			linhaSecao = sequencia-1;
	    	        	secaoAnterior = Indice.secao;
	        		} else {
	        			if(linha.length() < Indice.secao.length()){
	        				contador = sequencia;
	        				while(!saida){					
	        					linhaDummy = bufferFormatado.get(contador).trim();
								if(linhaDummy.startsWith("Código para aferir autenticidade deste caderno") || 
	        						linhaDummy.startsWith("Tribunal Regional do Trabalho da") ||
	        						linhaDummy.startsWith("Data da Disponibilização:")){
										contador++;
										continue;
								} else {
									if(Indice.secao.contains(linha.trim() + " " + linhaDummy.trim())){		        			
										linhaSecao = sequencia-1;
										secaoAnterior = Indice.secao;
										saida = true;
										break;
									}
								}		
	        				}
	        				linhaDummy = "";
	        			}
	        		}
	        	}

	        	if(Indice.grupo.equals(linha.trim()) && Indice.paginaGrupo == pagina){
	        		contador = sequencia;
	        		linhasLidas = 0;
					while(contador <= 30){
						linhaDummy = bufferFormatado.get(contador).trim();
						if(linhaDummy.startsWith("Código para aferir autenticidade deste caderno") || 
        						linhaDummy.startsWith("Tribunal Regional do Trabalho da") ||
        						linhaDummy.startsWith("Data da Disponibilização:")){
							contador++;
        					continue;
        				} else {	
    						if(obtemNumeroProcesso(linhaDummy) != null || linha.equals("Portaria")){
    							break;       					
	        				}
	        			}
        				contador++;
        				linhasLidas++;
        			}						
					linhaDummy = "";						
					if(linhasLidas <= 30){
	    	        	if(secaoAnterior != null){	
		    	        	regIndice = localizaIndice(Indice.secao, Indice.complementoSecao, Indice.grupo, Indice.paginaSecao, Indice.paginaGrupo);  	
		    	        	if(regIndice >= 0){
		    	        		Index.get(regIndice).setLinhaSecao(linhaSecao);
		    	        		Index.get(regIndice).setLinhaGrupo(sequencia-1);
		    	        		saida = true;
		    	        		break;		    	        		
		    	        	}
	    	        	}
					} else {
						continue;
					}
		        }
        	}				// fim do while
        }					// fim do for
        Comuns.apresentaMenssagem("Fim do mapeamento de linhas.", tipoProcessamento, "informativa", null);
        Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
        registraLog("Fim do mapeamento de linhas.");
	}
		
	private static int localizaProximoGrupo(int numeroLinha){

		for (IndiceEdicao elemento : Index) {
			if(elemento.linhaSecao > numeroLinha){
				return elemento.linhaSecao;
			}				
			if(elemento.linhaGrupo > numeroLinha){
				return elemento.linhaGrupo;
			}
		} 
		return -1;
	}
		
	private static void carregaAssuntos() throws IOException{
		registraLog("Carga da tabela de assuntos");
		String linha = "";
		String linhaTratada = "";
		String [] arrayAssunto;
        FileInputStream arquivoIn = new FileInputStream(pastaEdicoes + "/subjects.txt");
		//BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn), "UTF-8"));
		BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn)));
        while(linha != null){
	    	linha = registro.readLine();
	    	
	    	if(linha == null) {
	    		break;
	    	} else {
	    		linhaTratada = formataPalavra(linha);
	    	}

    		if(!linhaTratada.contains("PODER JUDICIÁRIO") || linhaTratada.length() != 16){
    			if(!tabelaAssuntos.contains(linhaTratada)) {
    				tabelaAssuntos.add(linhaTratada);
    				arrayAssunto = linhaTratada.split(" ");
    				if(arrayAssunto.length > maiorAssunto) {
    					maiorAssunto = arrayAssunto.length;
    				}
    			}	    			
    		}	    			    	
        }
        registro.close();
	}

	private static void carregaAssuntosLocal() throws IOException{
		String linha = "";
		String linhaTratada = "";
		String [] arrayAssunto;
		String path = new File(".").getCanonicalPath();
	    FileInputStream arquivoIn = new FileInputStream(path+"/subjects.txt");
		BufferedReader registro = new BufferedReader(new InputStreamReader((arquivoIn), "UTF-8"));
	    
	    while(linha != null){
	    	linha = registro.readLine();
	    	
	    	if(linha == null) {
	    		break;
	    	} else {
	    		linhaTratada = formataPalavra(linha);
	    	}
	
			if(!linhaTratada.contains("PODER JUDICIÁRIO") || linhaTratada.length() != 16){
				if(!tabelaAssuntos.contains(linhaTratada)) {
					tabelaAssuntos.add(linhaTratada);
					arrayAssunto = linhaTratada.split(" ");
					if(arrayAssunto.length > maiorAssunto) {
						maiorAssunto = arrayAssunto.length;
					}
				}	    			
			}	    			    	
	    }
	    registro.close();
	}

	private static void inicializaPublicacao() throws Exception {
		registraLog("Publicação inicializada " + seqEdicao);
		publicacao.setTribunal(strTribunal);
		publicacao.setSeqEdicao(seqEdicao);
		publicacao.setDescricao(descricaoFolder);
		publicacao.setEdicao(DtEdicao);
		publicacao.setStrEdicao(strEdicao);
		publicacao.setFolder(edtFolderName);
		publicacao.setCliente(cliente);
		editalFolder = InterfaceServidor.verificaPastaEdicao(sessao, pastaCarregamento, edtFolderName, descricaoFolder, strTribunal, DtEdicao);
	}
	
	private static void registraLog(String registroLog) {
		log.add(obtemHrAtual() + " - " + registroLog);
	}

	private static void gravaIndiceXML() throws IOException{
		String fileName = completaEsquerda(tribunal,'0',2) + "-Indice.xml"; 
	//	Comuns.gravaArquivoTexto(pastaIndiceXML,  fileName, edicaoXML);
		Comuns.gravaArquivoTexto(logFolder,  fileName, edicaoXML);
		edicaoXML.clear();
	}

/*
	private static void gravaIndice() throws IOException{
		
		String fileName = "C:\\srv\\kraio\\indice.txt";
		//FileWriter fileW = new FileWriter ("C:\\srv\\kraio\\indice.txt");
		//BufferedWriter buffW = new BufferedWriter (fileW);
		FileWriter fileW = null;
		BufferedWriter buffW = null;
		try {
			fileW = (FileWriter) new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
			for (IndiceEdicao Indice : Index) {
				buffW.write (Indice.secao + " - " + Indice.linhaSecao + " - " + Indice.grupo + " - " + Indice.linhaGrupo);
	            buffW.newLine ();
			}
	        buffW.close ();
		} catch (IOException io)
        {
		//	JOptionPane.showMessageDialog(null, "Falha na gravação do indice ");
        }
		
	}
*/
/*	Método enviaEdital em desenvolvimento, juntamente com a formatação do texto da publicação
	private static void enviaEdital() throws ParseException, IOException{

		int ultimoSequencial = 1;
		String nomeFile = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt";
		String textoDummy = "";
		String introducao = "";
		String linhaDummy = "";
		base.setFileName(nomeFile);
				
		Edital.setTitulo1(titulo1);
		Edital.setTitulo2(titulo2);
		Edital.setTitulo3(titulo3);
		Edital.setTitulo4(titulo4);
		Edital.setTitulo5(titulo5);
		Edital.setStrEdicao(strEdicao);
    	Edital.setVara(secao);
    	Edital.setGrupo(grupo);
		Edital.setAssunto(assunto);
		Edital.setAtores(atores);
		Edital.setIntimados(intimados);
    	Edital.setProcesso(processo);

		String linhaTraco =  "------------------------------------------------------------------------------------------------------------------------";
		String linhaRodaPe = "SplitDO " + versaoSplitter + "                  V&C Consultoria Ltda. (81) ‭982 386 404                             vconsulte@gmail.com‬" ;

		textoSaida.add(titulo1);
		textoSaida.add(titulo2);
		textoSaida.add(titulo3);
		textoSaida.add(titulo4);
		textoSaida.add(titulo5 + "\n");
		textoSaida.add(linhaTraco + "\n");
		textoSaida.add(secao);
		textoSaida.add(grupo);
		textoSaida.add(assunto + " " + complementoAssunto  + "\n");
		textoSaida.add(processo);
		textoSaida.add(linhaPauta);
		textoSaida.add(atores + "\n");
		textoSaida.add(intimados);

		if(textoIntroducao.size() > 0) {
			textoSaida.add(formataParagrafo(textoIntroducao));
			textoSaida.add("\n");
		}
		
		if(paragrafos.size() > 0) {
			for(String linha : paragrafos){
				textoSaida.add(linha);
			}
		}
		
		if(!linhaParagrafo.isEmpty() && pauta) {
			textoSaida.add(linhaParagrafo);
		}

		textoSaida.add(" \n");
		//textoSaida.add(cliente);				Não pode porque vai confundir o CLIPPING
		textoSaida.add(linhaTraco);
		textoSaida.add(linhaRodaPe);
		textoSaida.add(" \n");

		Edital.setTexto(textoSaida);
		
		if (InterfaceServidor.incluiEdital(sessao, editalFolder)) {
			if((sequencialSaida - ultimoSequencial == 100) || sequencialSaida == 1) {
				ultimoSequencial = sequencialSaida;
				registraLog("publicação " + sequencialSaida + " enviada com sucesso");
			}
		} else {
			msgWindow.incluiLinha("Houve erro na gravação da publicação Nº " + sequencialSaida + " Gravado com sucesso");
			registraLog("Erro no envio da publicação " + sequencialSaida );
			finalizaProcesso();
		}

		//msgWindow.incluiLinha(obtemHrAtual() + " Publicação nº: " + seqPublicacao);
		textoSaida.clear();
	}
*/
	
	private static boolean examinaPublicacao(ArrayList<String> texto) {
		boolean processoLocalizado = false;
		boolean processoRepetido = false;
		boolean assuntoLocalizado = false;
		boolean assuntoRepetido = false;

		String linha = "";
		
		for(int x=0; x<=texto.size()-1; x++) {
			if(linha != null) {
				if(validaAssunto(linha)) {
					if(validaAssunto(linha) && assuntoLocalizado) {
						assuntoRepetido = true;
					}
					if(validaAssunto(linha) && !assuntoLocalizado) {
						assuntoLocalizado = true;
					}
					continue;
				}
				
				linha = obtemNumeroProcesso(texto.get(x));
				if(linha != null && linha.equals(processoNumero) && !processoLocalizado) {
					processoLocalizado = true;
				}
				if(processoLocalizado && linha != null) {
					processoRepetido = true;
				}
				continue;
			} else {
				continue;
			}
		}
		
		if(assuntoLocalizado && processoLocalizado) {
			return true;
		}
		return false;
	}
	
	private static void registraIndiceXML() throws ParseException, IOException{
		String idArquivo = "";

		// header do arquivo no indice.xml da edição em curso
		idArquivo = strTribunal + "-" + seqEdicao + "-" + seqPublicacao;
		
		registraLog("Enviado publicação " + idArquivo);
		
		edicaoXML.add("\t\t" + "<field name="+"\"id"+"\""+">"+idArquivo+"</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"nome_arquivo"+"\""+">"+idArquivo+".txt</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"tribunal"+"\""+">" + formataPalavra(completaEsquerda(tribunal,'0',2)) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"edicao"+"\""+">" + formataPalavra(strEdicao) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"vara"+"\""+">" + formataPalavra(secao) + "</field>");		
		edicaoXML.add("\t\t" + "<field name="+"\"grupo"+"\""+">" + formataPalavra(grupo) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"assunto"+"\""+">" + formataPalavra(assunto) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"atores"+"\""+">" + formataPalavra(atores) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"intimados"+"\""+">" + formataPalavra(intimados) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\"processo"+"\""+">" + formataPalavra(processoNumero) + "</field>");
		edicaoXML.add("\t\t" + "<field name="+"\""+"texto"+"\""+">");
		//----------------------------------------------------------------------------------------------------------------------------

		// corpo da publicacao
		if(textoIntroducao.size() > 0) {
			for(String linha : textoIntroducao){
				edicaoXML.add("\t\t" + "\""+formataPalavra(linha)+"\"");
			}
		}
		if(textoEdital.size() > 0) {
			for(String linha : textoEdital){
				edicaoXML.add("\t\t" + "\""+formataPalavra(linha)+"\"");
			}
		}
		
		if(!linhaParagrafo.isEmpty() && pauta) {
			edicaoXML.add("\t\t" + "\""+formataPalavra(linhaParagrafo)+"\"");
		}
		//-----------------------------------------------------------------------------------------------------------------------------
	}
	
	private static void enviaPublicacao() throws ParseException, IOException{
		String labelFile = "";		
		String nomeArquivo = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt";
		int ultimoSequencial = 1;
	
		if((processoLinha.isEmpty() || textoEdital.isEmpty()) && !grupo.equals("Pauta")) {
			labelFile = "verificar";
		} else {
			labelFile = "Publicação";
		}
		
		registraIndiceXML();
		
		registraLog("Enviado publicação " + nomeArquivo);
		base.setFileName(strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".txt");

		publicacao.setTitulo1(titulo1);
		publicacao.setTitulo2(titulo2);
		publicacao.setTitulo3(titulo3);
		publicacao.setTitulo4(titulo4);
		publicacao.setTitulo5(titulo5);								
		publicacao.setStrEdicao(strEdicao);							
		publicacao.setVara(secao);									
		publicacao.setGrupo(grupo);									
		publicacao.setAssunto(assunto);								
		publicacao.setAtores(atores);								
		publicacao.setIntimados(intimados);							
		publicacao.setProcessoNumero(processoNumero);				
		publicacao.setProcessoLinha(processoLinha);					
		publicacao.setLabel(labelFile);
    	publicacao.setTipoDocumento(tipoDocumento);
    	publicacao.setCliente(cliente);
    	publicacao.setTribunal(completaEsquerda(tribunal,'0',2));
    	publicacao.setFolderName(pastaSaidaPDF);
    	publicacao.setFolder(pastaSaidaPDF);
    	publicacao.setNumeroOrdem(sequencialSaida);

		String linhaTraco =  "------------------------------------------------------------------------------------------------------------------------";
		String linhaRodaPe = "SplitDO " + versaoSplitter + "                   V&C Consultoria Ltda. (81) ‭982 386 404                             vconsulte@gmail.com‬" ;

		if(tipoConexao.equals("REMOTO")) {
			textoSaida.add(titulo1);
			textoSaida.add(titulo2);
			textoSaida.add(titulo3);
			textoSaida.add(titulo4);
			textoSaida.add(titulo5 + "\n");
			textoSaida.add(linhaTraco + "\n");
			textoSaida.add(secao);
			textoSaida.add(grupo);
		}
				
		if(!ordemDePauta.isEmpty() || !linhaDepoisLinhaProcesso.isEmpty()) {
			if(!linhaAntesAssunto.isEmpty()) {
				textoSaida.add(linhaAntesAssunto);
			}			
			textoSaida.add(assunto + " " + complementoAssunto  + "\n");
			if(textoIntroducao.size() > 0) {
				for(String linha : textoIntroducao){
					textoSaida.add(linha);
				}
				textoSaida.add("\n");
			}
			textoSaida.add(ordemDePauta);
			textoSaida.add(processoLinha);
			if(!linhaDepoisLinhaProcesso.isEmpty()) {
				textoSaida.add(linhaDepoisLinhaProcesso);
	    	}
			if(tipoConexao.equals("REMOTO")) {
				textoSaida.add(atores + "\n");
				textoSaida.add(intimados);
			}
		} else {
			textoSaida.add(assunto + " " + complementoAssunto  + "\n");
			if(textoIntroducao.size() > 0) {
				for(String linha : textoIntroducao){
					textoSaida.add(linha);
				}
			}
			textoSaida.add(processoLinha);
			if(!linhaDepoisLinhaProcesso.isEmpty()) {
				textoSaida.add(linhaDepoisLinhaProcesso);
	    	}
			
			textoSaida.add(linhaPauta);
			if(tipoConexao.equals("REMOTO")) {
				textoSaida.add(atores + "\n");
				textoSaida.add(intimados);
			}	
		}

		if(textoEdital.size() > 0) {
			for(String linha : textoEdital){
				textoSaida.add(linha);
			}
		}
		
		if(!linhaParagrafo.isEmpty() && pauta) {
			textoSaida.add(linhaParagrafo);
		}

		textoSaida.add("\n\n\t\t\t\t\t\t--- * ---"+"\n");
		textoSaida.add("Esta publicação encontra-se na página nº: " + paginaPublicacao + " do Diário Oficial do TRT " + strTribunal + " / Edição nº " + numeroEdicao + " de " + dataEdicao);		
		registraLog("página da publicação: " + paginaPublicacao + " - Trubunal: " + strTribunal + " - Nº Edição: " + numeroEdicao + " - Edição: " + dataEdicao + "\n");
		textoSaida.add(linhaTraco);
		textoSaida.add(linhaRodaPe);
		textoSaida.add("\n(Sc:" + sequencialSecao + "/Gr:" + sequencialGrupo + "/As:" + sequencialAssunto + "/Pr:" + sequencialProcesso + "/Pg:" + paginaPublicacao + ")");

		publicacao.setTexto(textoSaida);
		if(!examinaPublicacao(textoSaida)) {
			publicacao.setObservacoes("examinar edital");
		}

		if(tipoConexao.equals("REMOTO")) {
			if (InterfaceServidor.incluiPublicacao(sessao, editalFolder)) {
				if((sequencialSaida - ultimoSequencial == 100) || sequencialSaida == 1) {
					ultimoSequencial = sequencialSaida;
					registraLog("++++ publicação " + sequencialSaida + " enviada com sucesso ++++");
				}
			} else {
				Comuns.apresentaMenssagem("Houve erro na gravação da publicação Nº " + sequencialSaida + " Gravado com sucesso", tipoProcessamento, "informativa", null);
				registraLog("@@@ Erro no envio da publicação @@@" + sequencialSaida );
				finalizaProcesso();
			}
		} else {
			GravaXml.main(pastaSaida + "/" + pastaSaidaPDF + "/", true, dataEdicao.replace("/", "-"));
			GravaTexto.main(pastaSaida + "/" + pastaSaidaPDF + "/" + nomeArquivo, sequencialSecao, sequencialGrupo, sequencialAssunto, sequencialProcesso, versaoSplitter);
		}
		
		if(textoEdital.size() < 1000) {
			registraLog("verificar publicacao");
		}
		if(processoNumero.isEmpty()) {
			registraLog("publicacao sem nº de processo");
		}
		
		//msgWindow.incluiLinha(obtemHrAtual() + " Publicação nº: " + seqPublicacao);
		textoSaida.clear();
		listaDePublicacoes.add("Publicação: " + nomeArquivo + " página nº: " + paginaPublicacao + " do Diário Oficial do TRT " + strTribunal + " / Edição nº " + numeroEdicao + " de " + dataEdicao);
	}
	
	private static boolean validaParagrafo(int sequencia) {
		
		int referencia = sequencia;
		boolean saida = false;
		String linhaDummy = carregaLinha(referencia, false,3730);
	
		if(ehMaiuscula(linhaDummy)) {
			while(!saida){
				if(linhaDummy.charAt(linhaDummy.length()-1) == '.') {
					return true;
				} else {
					linhaDummy = carregaLinha(referencia++, false,3737);
				}
			}
		}
		
		return false;
	}
	
	private static boolean validaPublicacao(ArrayList<String> texto) {			// em desenvolvimento
		
		/**
		 * Desenvolver método que avalie se a publicação estar consistente, caso contrario e mesma será gravada com
		 * um indicador no nome do arquivo para sinaliza-lo que há suspeita de erro.
		 */
		
		
		return false;
	}

	private static void fechaPublicacao(ArrayList<String> texto) throws Exception, IOException {
		registraLog("Fechando publicação.");
		if(tipoConexao.equals("REMOTO")) {
			enviaPublicacao();
		} else {
			if (tipoArquivoSaida.equals("PDF")) {
				gravaPublicacao(texto);
			} else {
				enviaPublicacao();
			}
		}

		if(edital.size()<5 && !formataPalavra(assunto).equals("pauta de julgamento")) {
			registraLog(" edital " + sequencialSaida + " texto vazio");
		}
		if(atores == null) {
			registraLog(" edital " + sequencialSaida + " publicação sem atores");
		}
		if(intimados == null) {
			registraLog(" edital " + sequencialSaida + " publicação sem intimados");
		}
		if(processoNumero == null) {
			registraLog(" edital " + sequencialSaida + " publicacao sem processo");
		}
		
		registraLog("Publicação fechada " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processoNumero);

		sequencialSaida++;
		atores = "";
		intimados = "";
		atoresOK = false;
		intimadosOK = false;
		sequencialAssunto = 0;
		dtValida = false;
		textoEdital.clear();
		paragrafos.clear();
		linhaParagrafo = "";
		linhaDepoisLinhaProcesso = "";
		processoLinha = "";
		processoNumero = "";
//		processoDummy = null;
		paginaPublicacao = "";
		registraLog("Publicação fechada e enviada/gravada.\n");
		registraLog("-----------------------------------------------------------------------------------" + "\n");
	}

	private static boolean gravaPublicacao(ArrayList<String> texto) throws Exception {
		
		String nomeFile = strTribunal + "-" + seqEdicao + "-" + seqPublicacao + ".pdf";
		registraLog("Gravando publicação " + nomeFile);
		String dummy = "";
		publicacao.setTitulo1(titulo1);
		publicacao.setTitulo2(titulo2);
		publicacao.setTitulo3(titulo3);
		publicacao.setTitulo4(titulo4);
		publicacao.setTitulo5(titulo5);
		publicacao.setStrEdicao(strEdicao);
		publicacao.setVara(secao);
		publicacao.setGrupo(grupo);
		publicacao.setAssunto(assunto);
		publicacao.setProcessoNumero(processoNumero);
		publicacao.setProcessoLinha(processoLinha);

    	if(!atores.isEmpty()) {
    		dummy  = atores.replaceAll("&","e");
    		publicacao.setAtores(dummy);
    	} else publicacao.setAtores("---");
    	if(!intimados.isEmpty()) {
    		dummy  = intimados.replaceAll("&","e");
    		publicacao.setIntimados(dummy);
    	} else publicacao.setIntimados("---");
    	
    	publicacao.setTexto(formataTexto(edital));
    	publicacao.setTipoDocumento(tipoDocumento);
    	publicacao.setCliente(cliente);
    	if(!textoIntroducao.isEmpty()) {
    		publicacao.setIntroducao(formataTexto(textoIntroducao));
    	}
    	base.setFileName(nomeFile);
    	base.setPastaPDF(pastaSaidaPDF);

		SalvaPdf.gravaPdf();
		registraLog("arquivo PDF salvo");
//		GravaXml.main(pastaSaida + "/" + pastaSaidaPDF + "/");        <<<<<<<<<<<<< ATENCAO
		registraLog("arquivo XML saldo");
		registraLog("Publicação " + sequencialSaida + " / " + secao + " / " + grupo + " / " + assunto + " / " + processoLinha);        
		edital.clear();
		registraLog("Ppublicação " + nomeFile + "gravada");
		return true;
	}
		
	private static ArrayList<String> formataPublicacao(ArrayList<String> buffer) throws Exception {
		registraLog("Formatação da Publicação");
		String linha = "";
		String dummy = "";
		int ix = 0;
		boolean parteInicial= false;
		boolean parteFinal = false;
		boolean hasLowercase = false;
		ArrayList<String> editalFormatado = new ArrayList<String>();

		while(ix <= buffer.size()-1) {
			if(buffer.get(ix).equals("")) {
				ix++;
				continue;
			}
			if(ix <= buffer.size()-1) {
				if(verificaDataValida(buffer.get(ix))){
					if(!assunto.equals("acordao") && ((buffer.size()-1)-ix) <= 2) {
						for(int i = ix; i<= buffer.size()-1; i++) {
							editalFormatado.add(buffer.get(i).trim());
						}
						break;
					} else {
						editalFormatado.add(buffer.get(ix).trim());
						ix++;
						continue;
					}
				}
				
				if((contaPalavras(buffer.get(ix).trim()) == 1) && buffer.get(ix).trim().equals("Assinatura") &&
						((buffer.size()-1) - ix) > 2) {
					editalFormatado.add(buffer.get(ix).trim());
					parteFinal = true;
					ix++;
					continue;
				}
				
				// Formatação da 1ª parte do edital ------------------------------------------
				if(obtemNumeroProcesso(buffer.get(ix)) == null) {
					if(!parteInicial) {
						dummy = formataPalavra(buffer.get(ix).trim());
						if((contaPalavras(dummy) == 1) && (dummy.equals("poder") || dummy.equals("judiciario"))) {	
							editalFormatado.add("PODER JUDICIÁRIO");
							ix = ix + 2;
							if(ix > buffer.size()-1) {
								break;
							} else {
								ix++;
								continue;
							}
						}					
						if((contaPalavras(buffer.get(ix).trim()) == 1) && !verificaPontoFinal(buffer.get(ix).trim())) {	// fundamentação
							editalFormatado.add(buffer.get(ix).trim());
							dummy = buffer.get(ix).trim();
							ix++;
							if(ix > buffer.size()-1) {
								break;
							} else {
								ix++;
								continue;
							}
						}						
						editalFormatado.add(buffer.get(ix).trim());
						parteInicial = true;
						ix++;
						if(ix > buffer.size()-1) {
							break;
						}
					}
					if(parteFinal) {												// Finalização do edital
						if(!linha.isEmpty()) {
							editalFormatado.add(linha);
						}
						editalFormatado.add(buffer.get(ix).trim());
						ix++;
						linha = "";
						if(ix > buffer.size()-1) {
							break;
						} else {
							ix++;
							continue;
						}
					}
					if(!buffer.get(ix).trim().isEmpty()) {
						dummy = buffer.get(ix).trim();
						hasLowercase = !dummy.equals(dummy.toUpperCase());				// se negativo -> ñ tem Lowcase
						if(!hasLowercase) {												// qdo a linha só tem Uppercase
							if((verificaPontoFinal(buffer.get(ix).trim()) && !hasLowercase)) {
								linha = linha + " " + buffer.get(ix).trim();
			 				} else { 
			 					if(!verificaPontoFinal(buffer.get(ix).trim()) && !hasLowercase) {
			 						if(ix+1 >= buffer.size()) {
			 							editalFormatado.add(linha);
			 							break;
			 						}
			 						dummy = buffer.get(ix+1).trim();
									hasLowercase = !dummy.equals(dummy.toUpperCase());
									if(!hasLowercase) {								// proxima linha é Uppercase ?
										if(linha.isEmpty()) {	
											editalFormatado.add(buffer.get(ix).trim() + " " + buffer.get(ix+1).trim());
										} else {
											editalFormatado.add(linha);
											editalFormatado.add(buffer.get(ix).trim() + " " + buffer.get(ix+1).trim());
										}

				 					} else {
				 						if(linha.isEmpty()) {	
											editalFormatado.add(buffer.get(ix).trim());

										} else {
											editalFormatado.add(linha);
											editalFormatado.add(buffer.get(ix).trim());
											linha = "";
										}
				 					}	
			 					}	
			 				}
							ix++;
							if(ix > buffer.size()-1) {
								break;
							} else {
								continue;
							}
						}
					}
				}	
			}	

			if(linha.isEmpty()) {
				linha = (buffer.get(ix).trim());
			} else {
				if(((buffer.size()-1) - ix) > 2) {
					linha = linha + " " + (buffer.get(ix).trim());
				} else {
					editalFormatado.add(linha);
					editalFormatado.add(buffer.get(ix).trim());
					linha = "";	
				}	
			}
			if(verificaPontoFinal(buffer.get(ix).trim()) || obtemNumeroProcesso(buffer.get(ix)) != null){
				linha = linha + "\n";
				editalFormatado.add(linha);
				linha = "";
			} 
			ix++;
			if(ix == buffer.size()-1) {
				if(linha.isEmpty()) {
					editalFormatado.add(linha);
				}
				editalFormatado.add(buffer.get(ix).trim());
				break;
			}
		}
		return editalFormatado;
	}
	
	private static String obtemHrAtual() {

		String hr = "";
		String mn = "";
		String sg = "";
		Calendar data = Calendar.getInstance();
		hr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		mn = Integer.toString(data.get(Calendar.MINUTE));
		sg = Integer.toString(data.get(Calendar.SECOND));

		return completaEsquerda(hr,'0',2)+":"+completaEsquerda(mn,'0',2)+":"+completaEsquerda(sg, '0', 2);
	}

	private static int comparaPalavrasAssunto(String assunto, String linha) {
		
		// Igualidades é o numero de palavras em tabelaAssuntos que existem na linha em ordem sequencial (inic -> fim) 
		
		String [] arrayLinha = linha.split(" ");
		String [] arrayAssunto = assunto.split(" ");
		int igualidades = 0;
		
		if(arrayLinha.length == 0) return 0;

		for(int x=0; x <= arrayAssunto.length-1; x++) {			// conta qtas palavras de linha existem em um assunto da tabela
			if(x > arrayLinha.length-1) {						// fim das palavras exietntes na linha
				break;
			}
			if(arrayAssunto[x].equals(arrayLinha[x])) {
				igualidades++;									// qtd de palavras iguais encontradas
			}			
		}
		return igualidades;		
	}
		
	private static boolean verificaSeLinhaTemNumProcesso(String linha) {
		String linhaDummy = formataPalavra(linha);
		String [] arrayLinha = linhaDummy.split(" ");

		if(arrayLinha.length == 0) {
			return false;
		} else {
			if(arrayLinha.length >= 2) {
				if((arrayLinha[0].equals("processo") && arrayLinha[1].equals("nº")) ||
					(arrayLinha[0].equals("processo:") ||
					(arrayLinha[0].equals("numero") &&arrayLinha[1].equals("do")) ||
					(arrayLinha[0].equals("proc.") && arrayLinha[1].equals("nº") && arrayLinha[2].equals("trt")))) {
		
					if(obtemNumeroProcesso(linha) != null) {
						return true;
					}
				}
			}
		}		
		return false;	
	}
		
	private static boolean verificaContinuacaoLinha(String linha) {
			
			String linhaFormatada = formataPalavra(linha);
			String [] arrayLinha = linhaFormatada.split(" ");
			String criterio = "";
			String palavraFinal = "";
			int kl = 0;

			if(arrayLinha.length == 0) return false;
			
			if(linha.lastIndexOf(',') == linha.length()-1 || linha.lastIndexOf('-') == linha.length()-1 || 
					linha.lastIndexOf(':') == linha.length()-1) {
				return true;
			}
			
			for(int x=0; x <= continuacoesPossiveis.size()-1; x++) {
				if(arrayLinha[arrayLinha.length-1].equals(continuacoesPossiveis.get(x))) {
					return true;
				}
			}

			palavraFinal = arrayLinha[arrayLinha.length-1];
			
			if(linha.length() >= 5 && palavraFinal.length() >= 4) {
				palavraFinal = arrayLinha[arrayLinha.length-1];
				kl = palavraFinal.length()-4;
				criterio = palavraFinal.substring(kl);
				if(arrayLinha[arrayLinha.length-1].substring(kl).equals("oab:")) {
					return true;
				}
			}
			return false;	
		}
			
	private static boolean verificaPontoFinal(String linha) {
		char ultimaLetra;
		String ultimaPalavra = "";
		String[] palavras;
		palavras = linha.split(" ");
		ultimaPalavra = palavras[palavras.length - 1];
		ultimaLetra = ultimaPalavra.charAt(ultimaPalavra.length()-1);
		if(ultimaLetra != '.') {
			return false;
		}
		return true;
	}
		
	private static void criaPastaSaidaPDF(File folder) {
        try {
        //  File diretorio = new File("/Users/avmcf/jaq/" + tribunal + "_" + edicao);
        //	File diretorio = new File(tribunal + "_" + edicao);
            if(folder.exists()) {
            	folder.delete();
            } 
            folder.mkdir();     
        } catch (Exception erro) {
        	Comuns.apresentaMenssagem("Falha ao criar pasta no repositório", tipoProcessamento, "erro",  erro.toString() );
        }
    }
	
	private static String quebraLinha(String texto) {
		String textoTruncado = "";
		String linhaPreparada = "";
		int var = texto.trim().split(" ", -1).length - 1;      
		String palavras[] = new String[var];                		
		palavras = texto.split(" ");
		texto = texto.trim();
		
		if(texto.length() <= tamanhoLinha){
			textoTruncado = texto;
		} else {
			for (int x=0; x <= palavras.length-1; x++) {
				if((tamanhoLinha - linhaPreparada.length()) <= palavras[x].length()){
					if(textoTruncado.isEmpty()) {
						textoTruncado = linhaPreparada + "\n";
					} else {
						textoTruncado = textoTruncado + " " + linhaPreparada + "\n";
					}
					textoTruncado = textoTruncado + " " + palavras[x].trim();
					linhaPreparada = "";
				} else {
					if(linhaPreparada.isEmpty()) {
						linhaPreparada = palavras[x];
					} else {
						linhaPreparada = linhaPreparada + " " + palavras[x];
					}				
				}
			}
			if(!linhaPreparada.isEmpty()) {
				textoTruncado = textoTruncado + " " + linhaPreparada;
			}
		}
		return textoTruncado;
	}
	
	private static String quebraLinhaT(String texto, int limite) {
		String textoTruncado = "";
		String linhaPreparada = "";
		int var = texto.trim().split(" ", -1).length - 1;      
		String palavras[] = new String[var];                		
		palavras = texto.split(" ");
		texto = texto.trim();
		
		if(texto.length() <= limite){
			textoTruncado = texto;
		} else {
			for (int x=0; x <= palavras.length-1; x++) {
				if((limite - linhaPreparada.length()) <= palavras[x].length()){
					textoTruncado = linhaPreparada + "\n";
					textoTruncado = textoTruncado + palavras[x].trim();
					linhaPreparada = "";
				} else {
					if(linhaPreparada.isEmpty()) {
						linhaPreparada = palavras[x];
					} else {
						linhaPreparada = linhaPreparada + " " + palavras[x];
					}				
				}
			}
			if(!linhaPreparada.isEmpty()) {
				textoTruncado = textoTruncado + " " + linhaPreparada;
			}
		}
		return textoTruncado;
	}
		
	private static String formataParagrafo(ArrayList<String> texto) {
		
		String linhaDummy = "";
		String textoJustificado = "";
		String linhaDoParagrafo = "";
		int limite = tamanhoLinha;
		int tamanhoAcumulado = 0;

		for (int x=0; x<=texto.size()-1; x++ ) {
			linhaDummy = texto.get(x);
			if(linhaDummy.length() > limite) {
				textoJustificado = quebraLinha(linhaDummy);
				continue;
			} else {
				if(linhaDoParagrafo.isEmpty()) {
					linhaDoParagrafo = linhaDummy;
					tamanhoAcumulado = linhaDummy.length();
				} else {
					linhaDoParagrafo = linhaDoParagrafo + " " + linhaDummy;
					tamanhoAcumulado = tamanhoAcumulado + linhaDummy.length();
					if(tamanhoAcumulado > limite) {
						if(textoJustificado.isEmpty()) {
							textoJustificado = quebraLinha(linhaDoParagrafo);
						} else {
							textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
						}
						linhaDoParagrafo = "";
						tamanhoAcumulado = 0;
					}
				}
			}
		}
		if(!linhaDoParagrafo.isEmpty()) {
			if(tamanhoAcumulado > limite) {
				if(textoJustificado.isEmpty()) {
					textoJustificado = quebraLinha(linhaDoParagrafo);
				} else {
					textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
				}
			} else {
				textoJustificado = textoJustificado + " " + quebraLinha(linhaDoParagrafo);
			}
		}
		return textoJustificado;
	}
		
	private static ArrayList<String> formataTexto(ArrayList<String> texto) {
		
		String linha = "";
		String linhaContinua = "";
		String [] palavras;
		int ip = 0;
		int ix = 0;
		int posicao = 0;
		boolean saida = true;

		ArrayList<String> textoFormatado = new ArrayList<String>();

		while (ix <= (texto.size()-1)){				
			linhaContinua = linhaContinua + texto.get(ix) +" ";
			ix++;
		}				
		saida = false;
		posicao = 0;

		linha = "";
		palavras = linhaContinua.split(" ");
		
		while(!saida) {
			
			if(linha.length() + palavras[posicao].length() <= 150) {		
				while(posicao <= palavras.length-1) {
					if(!linha.isEmpty()) {
						linha = linha + " " + palavras[posicao].trim();
					} else {
						linha = palavras[posicao].trim();
					}	
					if(contaPalavras(linha) > 1) {	
						if(!verificaPontoFinal(linha) && (posicao == palavras.length)) {
							textoFormatado.add(linha);
							break;
						}
						if(posicao < (palavras.length-1)) {
							if((linha.length() + palavras[posicao+1].length()) > 150) {
								textoFormatado.add(linha);
								linha = "";
							}
						} else {
							textoFormatado.add(linha);
							linha = "";
							break;
						}
						ip++;
					}
					posicao++;
				}						
			} else {
				saida = true;
			}
			if(posicao > palavras.length-1) {
				saida = true;
			}
		}							
		return textoFormatado;
	}
		
	private static String obtemTribunal(String linhaDummy){

		String sequencia = "";
		int i = 0;
		if (linhaDummy.startsWith("Caderno Judiciário do Tribunal")){
			if (!linhaDummy.contains("Superior")) {
				while (i <= linhaDummy.length()-1) {
					if ((linhaDummy.charAt(i) >= '0' && linhaDummy.charAt(i) <= '9')){
						sequencia = sequencia + linhaDummy.charAt(i);
					}
					i++;
				}
			} else {
				sequencia = "00";
			}
		}
		return sequencia;
	}
}	// final da classe SplitDO
	
	