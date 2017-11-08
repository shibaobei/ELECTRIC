package com.elec.utils;

import java.util.ArrayList;
import java.util.List;

import com.elec.entity.ElecFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;




public class LuceneUtils {

	/**向索引库中添加数据*/
	public static void addIndex(ElecFileUpload fileUpload) {
		//使用ElecFileUpload对象转换成Document对象
		Document document = FileUploadDocument.FileUploadToDocument(fileUpload);
		/**新增、修改、删除、查询都会使用分词器*/
		try {
			IndexWriterConfig indexWirterConfig = new IndexWriterConfig(Version.LUCENE_36,Configuration.getAnalyzer());
			IndexWriter indexWriter = new IndexWriter(Configuration.getDirectory(),indexWirterConfig);
			//添加数据
			indexWriter.addDocument(document);
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**使用查询条件，搜素索引库的数据，返回List<ElecFileUpload>*/
	/**
	 * 
	 * @param projId：所属单位
	 * @param belongTo：图纸类别
	 * @param queryString：文件名称和文件描述
	 * @return
	 * 
	 *  sql语句回顾：拼装条件，and o.projId = ? and o.beglongTo = ?
	 */
	public static List<ElecFileUpload> searcherIndexByCondition(String projId,
			String belongTo, String queryString) {
		//结果集
		List<ElecFileUpload> fileUploadList = new ArrayList<ElecFileUpload>();
		try {
			IndexSearcher indexSearcher = new IndexSearcher(IndexReader.open(Configuration.getDirectory()));
			//封装查询条件(使用BooleanQuery对象，连接多个查询条件)
			BooleanQuery query = new BooleanQuery();
			//条件一（所属单位）
			if(StringUtils.isNotBlank(projId)){
				//词条查询
				TermQuery query1 = new TermQuery(new Term("projId", projId));
				query.add(query1, Occur.MUST);//相当与sql语句的and
			}
			//条件二（图纸类别）
			if(StringUtils.isNotBlank(belongTo)){
				//词条查询
				TermQuery query2 = new TermQuery(new Term("belongTo", belongTo));
				query.add(query2, Occur.MUST);//相当与sql语句的and
			}
			
			//条件三（文件名称和文件描述）
			if(StringUtils.isNotBlank(queryString)){
				//多个字段进行检索的时候，查询使用QueryPaser
				QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,new String[]{"fileName","comment"},Configuration.getAnalyzer());
				Query query3 = queryParser.parse(queryString);
				query.add(query3, Occur.MUST);//相当与sql语句的and
			}
			
			//向索引库中搜素数据
			/**
			 * 参数一：指定的查询条件(lucene的写法）
			 * 参数二：返回前100条数据
			 */
			TopDocs topDocs = indexSearcher.search(query, 100);
			System.out.println("查询的总记录数："+topDocs.totalHits);
			//表示返回的结果集
			ScoreDoc [] scoreDocs = topDocs.scoreDocs;
			/**添加设置文字高亮begin*/
			//html页面高亮显示的格式化，默认是<b></b>
			Formatter formatter = new SimpleHTMLFormatter("<font color='red'><b>","</b></font>");
			//执行查询条件，因为高亮的值就是查询条件
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter,scorer);
			
			//设置文字摘要，此时摘要大小
			int fragmentSize = 10;
			Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
			highlighter.setTextFragmenter(fragmenter);
			/**添加设置文字高亮end*/
			if(scoreDocs!=null && scoreDocs.length>0){
				for(ScoreDoc scoreDoc:scoreDocs){
					System.out.println("相关度得分："+scoreDoc.score);//默认得分高的数据在前面
					//获取查询结果的文档的惟一编号，只有回去惟一编号，才能获取该编号对应的数据
					int doc = scoreDoc.doc;
					//使用编号，获取真正的数据
					Document document = indexSearcher.doc(doc);
					
					/**获取文字高亮的信息 begin*/
					//获取文字的高亮，一次只能获取一个字段高亮的结果，如果获取不到，返回null值
					//查找索引库字段为fileName
					String fileName = highlighter.getBestFragment(Configuration.getAnalyzer(), "fileName", document.get("fileName"));
					//如果null表示没有高亮的结果，如果高亮的结果，应该将原值返回
					if(StringUtils.isBlank(fileName)){
						fileName = document.get("fileName");
						if(fileName!=null && fileName.length()>fragmentSize){
							//截串，从0开始
							fileName = fileName.substring(0,fragmentSize);
						}
					}
					//将高亮后的结果放置到document中去
					document.getField("fileName").setValue(fileName);
					//查询索引库字段为comment
					String comment = highlighter.getBestFragment(Configuration.getAnalyzer(), "comment", document.get("comment"));
					//如果null表示没有高亮的结果，如果高亮的结果，应该将原值返回
					if(StringUtils.isBlank(comment)){
						comment = document.get("comment");
						if(comment!=null && comment.length()>fragmentSize){
							//截串，从0开始
							comment = comment.substring(0,fragmentSize);
						}
					}
					//将高亮后的结果放置到document中去
					document.getField("comment").setValue(comment);
					/**获取文字高亮的信息 end*/
					
					//将Document对象转换成javabean
					ElecFileUpload elecFileUpload = FileUploadDocument.documentToFileUpload(document);
					fileUploadList.add(elecFileUpload);
				}
			}
			indexSearcher.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return fileUploadList;
	}

	/**使用ID，删除索引库*/
	public static void deleteIndex(Integer seqId) {
		//使用词条删除
		String id = NumericUtils.intToPrefixCoded(seqId);
		Term term = new Term("seqId", id);
		/**新增、修改、删除、查询都会使用分词器*/
		try {
			IndexWriterConfig indexWirterConfig = new IndexWriterConfig(Version.LUCENE_36,Configuration.getAnalyzer());
			IndexWriter indexWriter = new IndexWriter(Configuration.getDirectory(),indexWirterConfig);
			//添加数据
			indexWriter.deleteDocuments(term);
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
}
