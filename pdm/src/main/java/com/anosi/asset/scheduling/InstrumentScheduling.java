package com.anosi.asset.scheduling;

import com.anosi.asset.i18n.I18nComponent;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Instrument;
import com.anosi.asset.service.InstrumentService;
import com.anosi.asset.service.impl.DeviceServiceImpl;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class InstrumentScheduling {


	@Autowired
	private ConvertRemindMail convertRemindMail;

	/***
	 * 每天0点进行预警检测
	 *
	 * @throws MessagingException
	 * @throws IOException
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
	 * @throws TemplateNotFoundException
	 * @throws TemplateException
	 */
	@Scheduled(cron = "0 0 9 * * ?")
	//@Scheduled(cron = "0/5 * * * * ? ")
	public void checkCalculate() throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {
		convertRemindMail.checkRemind();
	}

	@Component
	public static class ConvertRemindMail {

		@Autowired
		private InstrumentService instrumentService;
		@Autowired
		private JavaMailSender mailSender;
		@Autowired
		private I18nComponent i18nComponent;

		@Value("${spring.mail.username}")
		private String from;

		@Transactional
		public void checkRemind() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
				IOException, MessagingException, TemplateException {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
			// 设定去哪里读取相应的ftl模板
			cfg.setClassForTemplateLoading(this.getClass(), "/templates/mail/");
			// 在模板文件目录中寻找名称为name的模板文件
			Template template = cfg.getTemplate("instrumentRemind.ftl");

			for (Instrument instrument : instrumentService.findAll()) {
				// 判断是否检查预警,如果已经到了需要预警的天数,则发邮件
				if (instrument.needCheckRemind()) {
					for (Account account : instrument.getDevice().getRemindReceiverList()) {
						MimeMessage mimeMessage = mailSender.createMimeMessage();
						MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
						// 基本设置.
						helper.setFrom(from);// 发送者.
						helper.setTo(account.getEmailAddress());// 接收者.
						helper.setSubject(MessageFormat.format(i18nComponent.getMessage("instrument.checkRemindMessage"),
								instrument.getDevice().getSerialNo(), instrument.getName()));// 邮件主题.

						// 设置model
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("instrument", instrument);
						model.put("account", account);

						String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
						helper.setText(html, true);
						// 发送
						mailSender.send(mimeMessage);
					}
				}
			}
		}

	}

}
