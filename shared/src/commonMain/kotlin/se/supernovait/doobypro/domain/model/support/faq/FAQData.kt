package se.supernovait.doobypro.domain.model.support.faq

object FAQData {
    val faqs = listOf(
        // Getting Started
        FAQ(
            id = "faq_1",
            category = FAQCategory.GETTING_STARTED,
            question = "How do I create an account?",
            answer = "To create an account, open the app and follow the setup wizard on the first launch. Enter your name, email, and other required information. Your account will be created immediately."
        ),
        FAQ(
            id = "faq_2",
            category = FAQCategory.GETTING_STARTED,
            question = "Can I change my account information later?",
            answer = "Yes, you can update your account information anytime. Go to Settings > Account and tap on the section you want to edit. Make your changes and save."
        ),

        // Orders
        FAQ(
            id = "faq_3",
            category = FAQCategory.ORDERS,
            question = "How do I create a new order?",
            answer = "Tap the + button on the dashboard to create a new order. You can either scan a customer QR code or manually enter their details. Then fill in the order information and submit."
        ),
        FAQ(
            id = "faq_4",
            category = FAQCategory.ORDERS,
            question = "Can I edit an order after creating it?",
            answer = "Yes, you can edit orders in the New, In Progress, and Ready statuses. Open the order details and tap Edit. Some fields are locked once the order reaches Ready status."
        ),
        FAQ(
            id = "faq_5",
            category = FAQCategory.ORDERS,
            question = "What should I do if I create an order by mistake?",
            answer = "You can delete orders that are in the New status. Open the order, tap Delete, and confirm. Orders in other statuses cannot be deleted."
        ),

        // Delivery
        FAQ(
            id = "faq_6",
            category = FAQCategory.DELIVERY,
            question = "What are the delivery options?",
            answer = "We offer two delivery options: Standard and Express. Standard is the regular delivery option, while Express provides faster processing for customers who need their items urgently."
        ),
        FAQ(
            id = "faq_7",
            category = FAQCategory.DELIVERY,
            question = "What's the difference between in-store pickup and home delivery?",
            answer = "In-store pickup allows customers to collect their items from your location. Home delivery means the items will be delivered to the customer's address. Choose the method that works best for each order."
        ),
        FAQ(
            id = "faq_8",
            category = FAQCategory.DELIVERY,
            question = "How do I set delivery dates?",
            answer = "When creating an order, you can select any date you want as the delivery date. The app allows open date selection so you have full flexibility."
        ),

        // Account
        FAQ(
            id = "faq_9",
            category = FAQCategory.ACCOUNT,
            question = "How do I update my profile?",
            answer = "Go to Account > User Profile to update your personal information. You can change your name, phone, email, and other details. Your user ID is permanent and cannot be changed."
        ),
        FAQ(
            id = "faq_10",
            category = FAQCategory.ACCOUNT,
            question = "How do I update my company information?",
            answer = "Go to Account > Company Profile to update your company details including name, address, phone, email, and location. Company ID is permanent."
        ),
        FAQ(
            id = "faq_11",
            category = FAQCategory.ACCOUNT,
            question = "Where can I find my license information?",
            answer = "Your license details are available in Account > License. This section is read-only and shows your license status, tier, and validity dates."
        ),

        // Technical
        FAQ(
            id = "faq_12",
            category = FAQCategory.TECHNICAL,
            question = "The app is running slowly. What can I do?",
            answer = "Try these steps: 1) Close and reopen the app, 2) Clear the app cache in settings, 3) Make sure you have enough storage space, 4) Update to the latest version. If problems persist, contact support."
        ),
        FAQ(
            id = "faq_13",
            category = FAQCategory.TECHNICAL,
            question = "I'm having trouble logging in.",
            answer = "First, check your internet connection. Then verify your email and password are correct. If you've forgotten your password, use the password reset option. For continued issues, contact our support team."
        ),
        FAQ(
            id = "faq_14",
            category = FAQCategory.TECHNICAL,
            question = "How do I report a bug?",
            answer = "Go to Support > Support and select 'Bug Report' as the request type. Describe the issue in detail, including when it happens and what you were doing. Attach screenshots if possible."
        ),

        // Billing
        FAQ(
            id = "faq_15",
            category = FAQCategory.BILLING,
            question = "How are customers charged for orders?",
            answer = "Customers can be charged based on your pricing settings. You can set prices for services and delivery options. The app calculates totals automatically based on the service selected and delivery method."
        ),
        FAQ(
            id = "faq_16",
            category = FAQCategory.BILLING,
            question = "Can I change prices for services?",
            answer = "Yes, you can update service prices anytime through Settings > Services. Changes apply to new orders created after the update."
        ),

        // Other
        FAQ(
            id = "faq_17",
            category = FAQCategory.OTHER,
            question = "Is my data secure?",
            answer = "Yes, we take data security seriously. All communications are encrypted, and your information is stored securely. We comply with data protection regulations."
        ),
        FAQ(
            id = "faq_18",
            category = FAQCategory.OTHER,
            question = "How do I contact support?",
            answer = "You can reach our support team through the Support section in the app. Fill out the support form with your question or issue, and our team will get back to you within 24-48 hours."
        )
    )
}
