package com.elec.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.elec.dao.IElecFileUploadDao;
import com.elec.entity.ElecFileUpload;
import com.elec.service.IElecFileUploadService;
import com.elec.utils.DateUtils;
import com.elec.utils.FileUtils;
import com.elec.utils.LuceneUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;




@Service(IElecFileUploadService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecFileUploadServiceImpl implements IElecFileUploadService {

	/**资料图纸管理表Dao*/
	@Resource(name= IElecFileUploadDao.SERVICE_NAME)
	IElecFileUploadDao elecFileUploadDao;

	/**  
	* @Name: saveFileUpload
	* @Description: 保存资料图纸管理（管理员进行文件上传操作）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-08（创建日期）
	* @Parameters: ElecFileUpload:VO对象
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveFileUpload(ElecFileUpload elecFileUpload) {
		//获取当前时间，转换成String类型
		String progressTime = DateUtils.dateToString(new Date());
		// 1：文件上传
		// 2：组织PO对象，保存到资料图纸管理的数据库表中
		//获取所属单位
		String projId = elecFileUpload.getProjId();
		//获取图纸类别
		String belongTo = elecFileUpload.getBelongTo();
		//获取上传文件的数组
		File [] uploads = elecFileUpload.getUploads();
		//获取上传文件的文件名
		String [] uploadsFileNames = elecFileUpload.getUploadsFileName();
		//获取上传文件的描述
		String [] comments = elecFileUpload.getComments();
		//遍历循环组织PO对象，完成保存
		if(uploads!=null && uploads.length>0){
			for(int i=0;i<uploads.length;i++){
				//组织PO对象
				ElecFileUpload fileUpload = new ElecFileUpload();
				fileUpload.setProjId(projId);//所属单位
				fileUpload.setBelongTo(belongTo);//图纸类别
				fileUpload.setProgressTime(progressTime);//当前时间
				fileUpload.setFileName(uploadsFileNames[i]);//上传的文件名
				//文件上传的同时，返回路径path
				String fileUrl = FileUtils.fileUploadReturnPath(uploads[i], uploadsFileNames[i], "资料图纸管理");
				fileUpload.setFileUrl(fileUrl);//上传的路径
				fileUpload.setComment(comments[i]);//上传文件的描述
				//执行保存
				elecFileUploadDao.save(fileUpload);
				//添加lucene，向索引库中添加数据
				//LuceneUtils.addIndex(fileUpload);
			}
		}
		
	}
	
	/**  
	* @Name: findFileUploadListByCondition
	* @Description: 通过选择所属单位和图纸类别的查询条件，查询对应单位和图纸下的文件上传的列表，返回List<ElecFileUpload>
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-08（创建日期）
	* @Parameters: ElecFileUpload:VO对象
	* @Return: List<ElecFileUpload>：资料图纸的集合
	*/
	public List<ElecFileUpload> findFileUploadListByCondition(
			ElecFileUpload elecFileUpload) {
		//组织查询条件
		String condition = "";
		List<Object> paramsList = new ArrayList<Object>();
		//所属单位
		String projId = elecFileUpload.getProjId();
		if(StringUtils.isNotBlank(projId) && !projId.equals("0")){
			condition += " and o.projId=?";
			paramsList.add(projId);
		}
		//图纸类别
		String belongTo = elecFileUpload.getBelongTo();
		if(StringUtils.isNotBlank(belongTo) && !belongTo.equals("0")){
			condition += " and o.belongTo=?";
			paramsList.add(belongTo);
		}
		//数组
		Object [] params = paramsList.toArray();
		//排序，按照时间排序
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.progressTime", "asc");
		List<ElecFileUpload> list = elecFileUploadDao.findFileUploadListByCondition(condition,params,orderby);
		return list;
	}

	/**  
	* @Name: findFileByID
	* @Description: 使用主键ID，查询资料图纸信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-08（创建日期）
	* @Parameters: Integer：主键ID
	* @Return: ElecFileUpload：资料图纸的对象
	*/
	public ElecFileUpload findFileByID(Integer fileID) {
		return elecFileUploadDao.findObjectByID(fileID);
	}
	
	/**  
	* @Name: findFileUploadListByLuceneCondition
	* @Description: 使用lucene组织条件先查询索引库，使用主键ID查询数据库，返回List<ElecFileUpload>
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-08（创建日期）
	* @Parameters: ElecFileUpload：VO对象
	* @Return: List<ElecFileUpload>：存放文件上传的集合
	*/
	public List<ElecFileUpload> findFileUploadListByLuceneCondition(
			ElecFileUpload elecFileUpload) {
		//返回的结果集
		List<ElecFileUpload> fileUploadList = new ArrayList<ElecFileUpload>();
		//获取页面传递的值
		//所属单位
		String projId = elecFileUpload.getProjId();
		//图纸类别
		String belongTo = elecFileUpload.getBelongTo();
		//按文件名称和描述搜素
		String queryString = elecFileUpload.getQueryString();
		//1：获取页面传递的查询条件，组织查询条件，搜索索引库（将Document转换成ElecFileUpload对象），搜索的时候，返回List<ElecFileUpload>
		List<ElecFileUpload> list = LuceneUtils.searcherIndexByCondition(projId,belongTo,queryString);
		//2：遍历List<ElecFileUpload>，获取每个对象存放的seqId（主键ID），使用seqId查询对应的数据，返回ElecFileUpload（数据比较全），将ElecFileUpload对象封装成List集合，返回List<ElecFileUpload>
		if(list!=null && list.size()>0){
			for(ElecFileUpload fileUpload:list){
				//获取主键ID
				Integer seqId = fileUpload.getSeqId();
				//使用主键ID，查询数据库
				String condition = " and o.seqId=?";
				Object [] params = {seqId};
				List<ElecFileUpload> fileList = elecFileUploadDao.findFileUploadListByCondition(condition, params, null);
				//将索引库中查询的高亮，设置到查询数据库返回的ElecFileUpload对象中
				//主键查询只有惟一值
				ElecFileUpload upload = fileList.get(0);
				upload.setFileName(fileUpload.getFileName());
				upload.setComment(fileUpload.getComment());
				//添加集合
				fileUploadList.addAll(fileList);
			}
		}
		return fileUploadList;
	}
	
	/**  
	* @Name: deleteFileUploadByID
	* @Description: 删除资料图纸管理数据
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-08（创建日期）
	* @Parameters: Integer：主键ID
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteFileUploadByID(Integer seqId) {
		ElecFileUpload elecFileUpload = elecFileUploadDao.findObjectByID(seqId);
		//路径
		String path = ServletActionContext.getServletContext().getRealPath("")+elecFileUpload.getFileUrl();
		//1：删除附件
		if(StringUtils.isNotBlank(path)){
			File file = new File(path);
			if(file.exists()){
				file.delete();
			}
		}
		
		//2：删除数据库的数据
		elecFileUploadDao.deleteObjectByIds(seqId);
		//3：删除索引库（让数据库的数据与索引库同步）
		LuceneUtils.deleteIndex(seqId);
	}
}
