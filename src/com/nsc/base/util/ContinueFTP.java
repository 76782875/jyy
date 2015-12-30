package com.nsc.base.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;

/**
 * ֧�ֶϵ�������FTPʵ����
 * 
 * @author
 * @version 0.1 ʵ�ֻ����ϵ��ϴ�����
 * @version 0.2 ʵ���ϴ����ؽ��Ȼ㱨
 * @version 0.3 ʵ������Ŀ¼�����������ļ���������Ӷ������ĵ�֧��
 */
public class ContinueFTP {
	public FTPClient ftpClient = new FTPClient();
	private static ContinueFTP ftp;
	private static ContinueFTP downLoadFtp;

	public ContinueFTP() {
		// ���ý�������ʹ�õ����������������̨
		this.ftpClient.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out)));

	}

	/**
	 * ʵ���� ContinueFTPEx
	 * 
	 * @param unitCode
	 *            ��λ����
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static synchronized ContinueFTP getInstance()
			throws NumberFormatException, IOException {

		if (ftp == null) {
			ftp = new ContinueFTP();
		}

		Configurater config = Configurater.getInstance();

		ftp.connect(config.getConfigValue("HOSTNAME"), new Integer(config
				.getConfigValue("PORT")).intValue(), config
				.getConfigValue("USERNAME"), config.getConfigValue("PASSWORD"));

		return ftp;
	}



	public static synchronized ContinueFTP getDownLoadInstance(String hostName,
			int port, String userName, String passWord)
			throws NumberFormatException, IOException {

		if (downLoadFtp == null) {
			downLoadFtp = new ContinueFTP();
		}
		downLoadFtp.connect(hostName, port, userName, passWord);

		return downLoadFtp;
	}

	/**
	 * ���ӵ�FTP������
	 * 
	 * @param hostname
	 *            ������
	 * @param port
	 *            �˿�
	 * @param username
	 *            �û���
	 * @param password
	 *            ����
	 * @return �Ƿ����ӳɹ�
	 * @throws IOException
	 */
	public boolean connect(String hostname, int port, String username,
			String password) throws IOException {

		ftpClient.setControlEncoding("GBK");

		FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_NT);
		config.setServerLanguageCode("GBK");
		// ftpClient.configure(config);

		ftpClient.setDataTimeout(120000);

		ftpClient.connect(hostname, port);
		if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			if (ftpClient.login(username, password)) {
				return true;
			}
		}
		disconnect();
		return false;
	}

	/**
	 * ��FTP�������������ļ�,֧�ֶϵ��������ϴ��ٷֱȻ㱨
	 * 
	 * @param remote
	 *            Զ���ļ�·��
	 * @param local
	 *            �����ļ�·��
	 * @return �ϴ���״̬
	 * @throws IOException
	 */
	public DownloadStatus download(String remote, String local)
			throws IOException {
		// ���ñ���ģʽ
		ftpClient.enterLocalPassiveMode();
		// �����Զ����Ʒ�ʽ����
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result;

		// ���Զ���ļ��Ƿ����
		FTPFile[] files = ftpClient.listFiles(remote);
		if (files.length != 1) {
			Logger.getLogger(ContinueFTP.class).warn("Զ���ļ�������");
			return DownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);
		// ���ش����ļ������жϵ�����
		if (f.exists()) {
			long localSize = f.length();
			// �жϱ����ļ���С�Ƿ����Զ���ļ���С
			if (localSize >= lRemoteSize) {
				Logger.getLogger(ContinueFTP.class).warn("�����ļ�����Զ���ļ���������ֹ");
				return DownloadStatus.Local_Bigger_Remote;
			}

			Logger.getLogger(ContinueFTP.class).info("Ŀ���ļ�: "+f.getAbsolutePath());
			// ���жϵ�����������¼״̬
			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(remote);
			byte[] bytes = new byte[1024];
			long step = lRemoteSize < 100 ? 1 : lRemoteSize / 100;
			long process = localSize / step;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						Logger.getLogger(ContinueFTP.class).info(
								"���ؽ��ȣ�" + process);
					// TODO �����ļ����ؽ���,ֵ�����process������
				}
			}
			in.close();
			out.close();
			boolean isDo = ftpClient.completePendingCommand();
			if (isDo) {
				result = DownloadStatus.Download_From_Break_Success;
			} else {
				result = DownloadStatus.Download_From_Break_Failed;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(remote);
			byte[] bytes = new byte[1024];
			long step = lRemoteSize < 100 ? 1 : lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						Logger.getLogger(ContinueFTP.class).info(
								"���ؽ��ȣ�" + process);
					// TODO �����ļ����ؽ���,ֵ�����process������
				}
			}
			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();
			if (upNewStatus) {
				result = DownloadStatus.Download_New_Success;
			} else {
				result = DownloadStatus.Download_New_Failed;
			}
		}
		return result;
	}
	
	
	
	
	/**
	 * ��FTP���������أ������ȡ��
	 */ 
	public InputStream download(String remote)
			throws IOException {
		// ���ñ���ģʽ
		ftpClient.enterLocalPassiveMode();
		// �����Զ����Ʒ�ʽ����
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		// ���Զ���ļ��Ƿ����
		FTPFile[] files = ftpClient.listFiles(remote);
		if (files.length != 1) {
			Logger.getLogger(ContinueFTP.class).warn("Զ���ļ�������");
		}
		InputStream in = ftpClient.retrieveFileStream(remote);
//		ftpClient.completePendingCommand();
		return in;
	}
		
	

	
	
	/**
	 * ��Ŀ¼��������
	 * @param remote Զ��Ŀ¼
	 * @param local  �����ļ��ı��ص�ַ
	 * @return
	 * @throws IOException 
	 */
	public DownloadStatus downloadByFolder(String remote, String local) throws IOException{
		if(StringUtils.isBlank(remote) || StringUtils.isBlank(local))
			return DownloadStatus.Remote_File_Noexist;
		remote = remote.replaceAll("\\\\", "/");
		// ���ñ���ģʽ
		ftpClient.enterLocalPassiveMode();
		// �����Զ����Ʒ�ʽ����
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding("GBK");
		DownloadStatus result = null;
		
		// ���Զ���ļ��Ƿ����
		FTPFile[] files = ftpClient.listFiles(remote);
		for(FTPFile file : files){
			result = download(remote+"/"+file.getName(),local+File.separator+file.getName());
			//��������Ժ�ɾ��
			deleteFile(remote+"/"+file.getName());
		}
		return result;
	}
	
	
	/**
	 * �ж�Զ��Ŀ¼�Ƿ�Ϊ��
	 * @param remote
	 * @return
	 * @throws IOException
	 */
	public boolean indexDirIsEmpty(String remote) throws IOException{
		FTPFile[] files = ftpClient.listFiles(remote);
		if(files == null){
			return true;
		}
		if(files.length > 0 )
			return false;
		return true;
	}
	/**
	 * �ϴ��ļ���FTP��������֧�ֶϵ�����
	 * 
	 * @param local
	 *            �����ļ����ƣ�����·��
	 * @param remote
	 *            Զ���ļ�·����ʹ��/home/directory1/subdirectory/file.ext����
	 *            http://www.guihua.org /subdirectory/file.ext
	 *            ����Linux�ϵ�·��ָ����ʽ��֧�ֶ༶Ŀ¼Ƕ�ף�֧�ֵݹ鴴�������ڵ�Ŀ¼�ṹ
	 * @return �ϴ����
	 * @throws IOException
	 */
	public UploadStatus upload(String local, String remote) throws IOException {
		ftpClient.changeWorkingDirectory("/");
		// ����PassiveMode����
		ftpClient.enterLocalPassiveMode();
		// �����Զ��������ķ�ʽ����
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding("GBK");
		// ftpClient.setControlEncoding("utf-8");
		// ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

		// FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
		// conf.setServerLanguageCode("zh");

		UploadStatus result;
		// ��Զ��Ŀ¼�Ĵ���
		String remoteFileName = remote;
		remote = remote.replaceAll("\\\\", "/");

		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			// ����������Զ��Ŀ¼�ṹ������ʧ��ֱ�ӷ���
			if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
				return UploadStatus.Create_Directory_Fail;
			}
		}

		// ���Զ���Ƿ�����ļ�
		// FTPFile[] files = ftpClient.listFiles(new String(remoteFileName
		// .getBytes("GBK"), "iso-8859-1"));
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return UploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return UploadStatus.Remote_Bigger_Local;
			}

			// �����ƶ��ļ��ڶ�ȡָ��,ʵ�ֶϵ�����
			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			// ����ϵ�����û�гɹ�����ɾ�����������ļ��������ϴ�
			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		return result;
	}

	/**
	 * ɾ��Զ���ļ�
	 * 
	 * @param remoteFileName
	 * @return
	 * @throws IOException
	 */
	public DeleteFileStatus deleteFile(String remoteFileName)
			throws IOException {

		if (!ftpClient.deleteFile(remoteFileName)) {
			return DeleteFileStatus.Delete_Remote_Faild;
		}

		return DeleteFileStatus.Delete_Remote_Success;
	}

	/**
	 * �Ͽ���Զ�̷�����������
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	/**
	 * �ݹ鴴��Զ�̷�����Ŀ¼
	 * 
	 * @param remote
	 *            Զ�̷������ļ�����·��
	 * @param ftpClient
	 *            FTPClient ����
	 * @return Ŀ¼�����Ƿ�ɹ�
	 * @throws IOException
	 */
	public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient)
			throws IOException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		// if (!directory.equalsIgnoreCase("/")
		// && !ftpClient.changeWorkingDirectory(new String(directory
		// .getBytes("GBK"), "iso-8859-1"))) {
		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(directory)) {
			// ���Զ��Ŀ¼�����ڣ���ݹ鴴��Զ�̷�����Ŀ¼
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				// String subDirectory = new String(remote.substring(start, end)
				// .getBytes("GBK"), "iso-8859-1");
				String subDirectory = new String(remote.substring(start, end));

				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						Logger.getLogger(ContinueFTP.class).warn("����Ŀ¼ʧ��");
						return UploadStatus.Create_Directory_Fail;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				// �������Ŀ¼�Ƿ񴴽����
				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	/**
	 * �ϴ��ļ���������,���ϴ��Ͷϵ�����
	 * 
	 * @param remoteFile
	 *            Զ���ļ��������ϴ�֮ǰ�Ѿ�������������Ŀ¼���˸ı�
	 * @param localFile
	 *            �����ļ� File���������·��
	 * @param processStep
	 *            ��Ҫ��ʾ�Ĵ�����Ȳ���ֵ
	 * @param ftpClient
	 *            FTPClient ����
	 * @return
	 * @throws IOException
	 */
	public UploadStatus uploadFile(String remoteFile, File localFile,
			FTPClient ftpClient, long remoteSize) throws IOException {
		UploadStatus status;
		// ��ʾ���ȵ��ϴ�
		long step = localFile.length() < 100 ? 1 : (localFile.length() / 100);
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		// OutputStream out = ftpClient.appendFileStream(new String(remoteFile
		// .getBytes("GBK"), "iso-8859-1"));
		OutputStream out = ftpClient.appendFileStream(remoteFile);
		// �ϵ�����
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte[1024];
		int c;
		while ((c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			if (localreadbytes / step != process) {
				process = localreadbytes / step;
				Logger.getLogger(ContinueFTP.class).info("�ϴ�����:" + process);
				// TODO �㱨�ϴ�״̬
			}
		}
		out.flush();
		raf.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		if (remoteSize > 0) {
			status = result ? UploadStatus.Upload_From_Break_Success
					: UploadStatus.Upload_From_Break_Failed;
		} else {
			status = result ? UploadStatus.Upload_New_File_Success
					: UploadStatus.Upload_New_File_Failed;
		}
		return status;
	}

	public void rename(String remoteFile1, String remoteFile2)
			throws IOException {

		ftpClient.rename(remoteFile1, remoteFile2);

	}

	/**
	 * ���ļ�������������
	 * 
	 * @param remoteFileName
	 * @param outs
	 * @throws IOException
	 */
	public boolean loadFile(String remoteFileName, OutputStream outs)
			throws IOException {
		boolean retFlag = ftpClient.retrieveFile(remoteFileName, outs);
		return retFlag;
	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		ContinueFTP ftp = new ContinueFTP();
		ftp.connect("192.168.3.123", 21, "edm", "edm");
		
		// ���ñ���ģʽ
		ftp.ftpClient.enterLocalPassiveMode();
		// �����Զ����Ʒ�ʽ����
		ftp.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result;

		// ���Զ���ļ��Ƿ����
		FTPFile[] files = ftp.ftpClient.listFiles("/q08012905/");
		for(FTPFile file : files){
			
		}
		
		
		
	}
}
