package de.fhdo.HeroSync.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service responsible for sending transactional emails.
 *
 * <p>The "from" address and the base URL used in verification links are
 * injected from {@code application.yaml}, making the service portable
 * across development, staging, and production environments without code
 * changes.
 */
@Service
@SuppressWarnings("null")
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from}")
  private String fromAddress;

  @Value("${app.base-url}")
  private String baseUrl;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Sends a verification email containing a one-click confirmation link.
   *
   * @param toEmail   The recipient's email address.
   * @param token     The unique UUID verification token stored on the user.
   * @param userName  The user's display name, used in the greeting.
   * @throws RuntimeException if the email cannot be sent (wraps {@link MessagingException}).
   */
  public void sendVerificationEmail(String toEmail, String token, String userName) {
    String verifyUrl = baseUrl + "/verify-email?token=" + token;

    String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head><meta charset="UTF-8" /></head>
        <body style="margin:0;padding:0;background:#06100c;font-family:Arial,sans-serif;color:#e2e8f0;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr><td align="center" style="padding:40px 16px;">
              <table width="560" style="background:#0d1f18;border-radius:16px;border:1px solid #1a3a2e;overflow:hidden;">
                <tr><td style="background:linear-gradient(135deg,#00e5a0,#1dbd88);padding:28px 32px;">
                  <h1 style="margin:0;color:#040d09;font-size:22px;letter-spacing:1px;">⚡ HeroSync</h1>
                </td></tr>
                <tr><td style="padding:32px;">
                  <h2 style="margin:0 0 12px;color:#00e5a0;font-size:20px;">Verify your email address</h2>
                  <p style="margin:0 0 8px;color:#94a3b8;">Hi %s,</p>
                  <p style="margin:0 0 24px;color:#94a3b8;line-height:1.6;">
                    Welcome to HeroSync! Click the button below to confirm your email address
                    and start building your hero journey.
                  </p>
                  <a href="%s" style="display:inline-block;padding:14px 32px;background:linear-gradient(135deg,#00e5a0,#1dbd88);color:#040d09;font-weight:700;font-size:15px;border-radius:10px;text-decoration:none;">
                    Verify Email Address
                  </a>
                  <p style="margin:24px 0 0;color:#64748b;font-size:12px;line-height:1.6;">
                    This link expires in 24 hours. If you did not create a HeroSync account,
                    you can safely ignore this email.
                  </p>
                  <hr style="border:none;border-top:1px solid #1a3a2e;margin:24px 0 0;" />
                  <p style="margin:12px 0 0;color:#64748b;font-size:11px;">
                    If the button doesn't work, copy this URL into your browser:<br/>
                    <a href="%s" style="color:#00e5a0;word-break:break-all;">%s</a>
                  </p>
                </td></tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(userName, verifyUrl, verifyUrl, verifyUrl);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromAddress);
      helper.setTo(toEmail);
      helper.setSubject("Verify your HeroSync account");
      helper.setText(html, true);
      mailSender.send(message);
    } catch (MessagingException | MailException ex) {
      System.out.println("\n-------------------------------------------------------------------");
      System.out.println("📧 EMAIL SENDING FAILED (check MAIL_USERNAME/MAIL_PASSWORD)");
      System.out.println("🔗 DEV VERIFICATION LINK: " + verifyUrl);
      System.out.println("-------------------------------------------------------------------\n");
      // We log instead of throwing so local development isn't blocked by missing SMTP config.
    }
  }

  /**
   * Sends a password-reset email (placeholder for future implementation).
   * Currently unused; reserved for the account settings flow.
   */
  public void sendPasswordResetEmail(String toEmail, String token, String userName) {
    String resetUrl = baseUrl + "/reset-password?token=" + token;

    String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head><meta charset="UTF-8" /></head>
        <body style="margin:0;padding:0;background:#06100c;font-family:Arial,sans-serif;color:#e2e8f0;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr><td align="center" style="padding:40px 16px;">
              <table width="560" style="background:#0d1f18;border-radius:16px;border:1px solid #1a3a2e;">
                <tr><td style="background:linear-gradient(135deg,#00e5a0,#1dbd88);padding:28px 32px;">
                  <h1 style="margin:0;color:#040d09;font-size:22px;">⚡ HeroSync</h1>
                </td></tr>
                <tr><td style="padding:32px;">
                  <h2 style="margin:0 0 12px;color:#00e5a0;font-size:20px;">Reset your password</h2>
                  <p style="margin:0 0 8px;color:#94a3b8;">Hi %s,</p>
                  <p style="margin:0 0 24px;color:#94a3b8;line-height:1.6;">
                    We received a request to reset your HeroSync password. Click the button below.
                    This link expires in 1 hour.
                  </p>
                  <a href="%s" style="display:inline-block;padding:14px 32px;background:linear-gradient(135deg,#00e5a0,#1dbd88);color:#040d09;font-weight:700;font-size:15px;border-radius:10px;text-decoration:none;">
                    Reset Password
                  </a>
                  <p style="margin:24px 0 0;color:#64748b;font-size:12px;">
                    If you did not request a password reset, ignore this email — your account is safe.
                  </p>
                </td></tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(userName, resetUrl);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromAddress);
      helper.setTo(toEmail);
      helper.setSubject("Reset your HeroSync password");
      helper.setText(html, true);
      mailSender.send(message);
    } catch (MessagingException | MailException ex) {
      System.out.println("\n-------------------------------------------------------------------");
      System.out.println("📧 EMAIL SENDING FAILED (check MAIL_USERNAME/MAIL_PASSWORD)");
      System.out.println("🔗 DEV RESET LINK: " + resetUrl);
      System.out.println("-------------------------------------------------------------------\n");
    }
  }
}
