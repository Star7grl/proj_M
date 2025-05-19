package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // <<< НОВЫЙ ИМПОРТ
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Booking;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException; // <<< НОВЫЙ ИМПОРТ
import javax.mail.internet.MimeMessage; // <<< НОВЫЙ ИМПОРТ
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetLink(String toEmail, String token) {
        String resetLink = "http://localhost:5174/reset-password?token=" + token;
        MimeMessage message = mailSender.createMimeMessage(); // Используем MimeMessage
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true, "UTF-8"); // true для multipart, UTF-8 для кодировки
            helper.setTo(toEmail);
            helper.setSubject("Сброс пароля");

            // HTML-содержимое письма
            String htmlContent = String.format(
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4;'>" +
                            "<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); text-align: center;'>" +
                            "<h2 style='color: #333333;'>Восстановление пароля</h2>" +
                            "<p style='color: #555555; font-size: 16px;'>Здравствуйте,</p>" +
                            "<p style='color: #555555; font-size: 16px;'>Вы запросили сброс пароля для вашего аккаунта. Нажмите на кнопку ниже, чтобы продолжить:</p>" +
                            "<a href='%s' style='" +
                            "display: inline-block; " +
                            "background-color: #763ba7; " + // Фирменный цвет кнопки
                            "color: #ffffff !important; " + // Важно для некоторых почтовых клиентов
                            "padding: 12px 25px; " +
                            "text-decoration: none !important; " + // Важно для некоторых почтовых клиентов
                            "border-radius: 5px; " +
                            "font-size: 16px; " +
                            "font-weight: bold; " +
                            "margin-top: 20px; margin-bottom: 20px;" +
                            "border: none; cursor: pointer;" +
                            "box-shadow: 0 2px 4px rgba(0,0,0,0.1);' " +
                            "target='_blank'>" + // Открывать в новой вкладке
                            "Сбросить пароль" +
                            "</a>" +
                            "<p style='color: #555555; font-size: 14px;'>Если вы не запрашивали сброс пароля, пожалуйста, проигнорируйте это письмо.</p>" +
                            "<p style='color: #777777; font-size: 12px;'>Ссылка действительна в течение 10 минут.</p>" +
                            "<hr style='border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;'>" +
                            "<p style='color: #999999; font-size: 12px;'>С уважением,<br>Команда Gloria Hotel</p>" +
                            "</div>" +
                            "</body>" +
                            "</html>",
                    resetLink
            );

            helper.setText(htmlContent, true); // true означает, что это HTML

            mailSender.send(message);
            logger.info("HTML ссылка для сброса пароля отправлена на {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Ошибка при создании HTML письма для сброса пароля на {}: {}", toEmail, e.getMessage(), e);
            // Можно добавить резервную отправку простого текстового письма, если HTML не удался
            sendPlainTextFallbackResetLink(toEmail, resetLink);
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при отправке HTML ссылки для сброса пароля на {}: {}", toEmail, e.getMessage(), e);
            sendPlainTextFallbackResetLink(toEmail, resetLink);
        }
    }

    // Резервный метод для отправки простого текста, если HTML не удался
    private void sendPlainTextFallbackResetLink(String toEmail, String resetLink) {
        try {
            SimpleMailMessage fallbackMessage = new SimpleMailMessage();
            fallbackMessage.setTo(toEmail);
            fallbackMessage.setSubject("Сброс пароля (Резервное сообщение)");
            fallbackMessage.setText("Перейдите по ссылке для сброса пароля: " + resetLink + "\nСсылка действительна 10 минут.");
            mailSender.send(fallbackMessage);
            logger.info("Резервная текстовая ссылка для сброса пароля отправлена на {}", toEmail);
        } catch (Exception ex) {
            logger.error("Ошибка при отправке резервной текстовой ссылки для сброса пароля на {}: {}", toEmail, ex.getMessage(), ex);
        }
    }


    public void sendBookingReceivedEmail(Booking booking) {
        User user = booking.getUser();
        if (user == null || user.getEmail() == null) {
            logger.error("Невозможно отправить письмо: пользователь или email отсутствуют для бронирования ID {}", booking.getBookingId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage(); // Оставляем SimpleMailMessage для этих уведомлений, если HTML не требуется
        message.setTo(user.getEmail());
        message.setSubject("Ваше бронирование принято в обработку");

        String emailText = buildBookingEmailText(
                user,
                booking,
                "Благодарим вас за ваше бронирование! Оно принято и ожидает подтверждения администратором.",
                "Детали вашего бронирования:"
        );

        message.setText(emailText);
        try {
            mailSender.send(message);
            logger.info("Письмо о принятии бронирования ID {} в обработку отправлено на {}", booking.getBookingId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма о принятии бронирования ID {} на {}: {}", booking.getBookingId(), user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendBookingConfirmedEmail(Booking booking) {
        User user = booking.getUser();
        if (user == null || user.getEmail() == null) {
            logger.error("Невозможно отправить письмо: пользователь или email отсутствуют для бронирования ID {}", booking.getBookingId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваше бронирование подтверждено!");

        String emailText = buildBookingEmailText(
                user,
                booking,
                "Рады сообщить, что ваше бронирование было успешно подтверждено!",
                "Подтвержденные детали бронирования:"
        );

        message.setText(emailText);
        try {
            mailSender.send(message);
            logger.info("Письмо с подтверждением бронирования ID {} отправлено на {}", booking.getBookingId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма о подтверждении бронирования ID {} на {}: {}", booking.getBookingId(), user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendBookingRejectedEmail(Booking booking) {
        User user = booking.getUser();
        if (user == null || user.getEmail() == null) {
            logger.error("Невозможно отправить письмо: пользователь или email отсутствуют для бронирования ID {}", booking.getBookingId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваше бронирование отменено/отклонено"); // Общий заголовок для отклонения админом

        String emailText = buildBookingEmailText(
                user,
                booking,
                "Сообщаем вам, что ваше бронирование было отменено или отклонено администратором.",
                "Детали отмененного/отклоненного бронирования:"
        );

        message.setText(emailText);
        try {
            mailSender.send(message);
            logger.info("Письмо об отмене/отклонении бронирования ID {} отправлено на {}", booking.getBookingId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма об отмене/отклонении бронирования ID {} на {}: {}", booking.getBookingId(), user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendBookingCancelledByUserEmail(Booking booking) {
        User user = booking.getUser();
        if (user == null || user.getEmail() == null) {
            logger.error("Невозможно отправить письмо: пользователь или email отсутствуют для бронирования ID {}", booking.getBookingId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Вы отменили бронирование");

        String emailText = buildBookingEmailText(
                user,
                booking,
                "Вы успешно отменили ваше бронирование.",
                "Детали отмененного бронирования:"
        );

        message.setText(emailText);
        try {
            mailSender.send(message);
            logger.info("Письмо об отмене бронирования ID {} пользователем отправлено на {}", booking.getBookingId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма об отмене бронирования ID {} пользователем на {}: {}", booking.getBookingId(), user.getEmail(), e.getMessage(), e);
        }
    }


    private String buildBookingEmailText(User user, Booking booking, String introductoryMessage, String detailsHeader) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String checkInStr = booking.getCheckInDate() != null ? booking.getCheckInDate().format(dateFormatter) : "не указана";
        String checkOutStr = booking.getCheckOutDate() != null ? booking.getCheckOutDate().format(dateFormatter) : "не указана";

        String roomName = (booking.getRoom() != null && booking.getRoom().getRoomTitle() != null)
                ? booking.getRoom().getRoomTitle()
                : "не указана (только услуги)";

        String servicesStr;
        if (booking.getServices() != null && !booking.getServices().isEmpty()) {
            servicesStr = booking.getServices().stream()
                    .map(service -> service.getServiceName() + " (" + (service.getServicePrice() != null ? service.getServicePrice() + " руб." : "цена не указана") + ")")
                    .collect(Collectors.joining(", "));
        } else {
            servicesStr = "дополнительные услуги отсутствуют";
        }

        String totalPriceStr = booking.getTotalSum() != null ? booking.getTotalSum().toString() + " руб." : "не указана";
        String userFirstName = user.getFirstName() != null ? user.getFirstName() : (user.getUsername() != null ? user.getUsername() : "Клиент");
        String userLastName = user.getLastName() != null ? user.getLastName() : "";

        return String.format(
                "Уважаемый(ая) %s %s,\n\n" +
                        "%s\n\n" +
                        "%s\n" +
                        "Номер бронирования: %s\n" +
                        "Статус бронирования: %s\n" +
                        "Имя: %s %s\n" +
                        "Email: %s\n" +
                        "Забронированная комната: %s\n" +
                        "Дата заезда: %s\n" +
                        "Дата выселения: %s\n" +
                        "Выбранные услуги: %s\n" +
                        "Итоговая цена: %s\n\n" +
                        "С наилучшими пожеланиями,\n" +
                        "Ваш отель \"Gloria\".",
                userFirstName, userLastName,
                introductoryMessage,
                detailsHeader,
                booking.getBookingId(),
                translateStatus(booking.getStatus()),
                userFirstName, userLastName,
                user.getEmail(),
                roomName,
                checkInStr,
                checkOutStr,
                servicesStr,
                totalPriceStr
        );
    }

    private String translateStatus(String status) {
        if (status == null) return "неизвестен";
        switch (status.toUpperCase()) {
            case "PENDING":
                return "В ОБРАБОТКЕ";
            case "CONFIRMED":
                return "ПОДТВЕРЖДЕНО";
            case "REJECTED":
                return "ОТМЕНЕНО";
            default:
                return status;
        }
    }
}