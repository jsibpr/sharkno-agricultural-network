import React from 'react';

const Privacy = () => {
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Privacy Policy - SharkNo Agricultural Network</h1>
      
      <div className="space-y-6 text-gray-700">
        <section>
          <h2 className="text-2xl font-semibold text-gray-900 mb-4">Information We Collect</h2>
          <p>SharkNo Agricultural Network collects information to provide better services to agricultural professionals:</p>
          <ul className="list-disc pl-6 mt-2 space-y-2">
            <li>Profile information (name, email, agricultural role)</li>
            <li>Professional experience in agriculture</li>
            <li>Agricultural skills and certifications</li>
            <li>LinkedIn profile data (when connected)</li>
            <li>Service listings and validations</li>
          </ul>
        </section>

        <section>
          <h2 className="text-2xl font-semibold text-gray-900 mb-4">How We Use Your Information</h2>
          <ul className="list-disc pl-6 space-y-2">
            <li>Connect agricultural professionals</li>
            <li>Validate professional skills and experience</li>
            <li>Provide agricultural service marketplace</li>
            <li>Improve our platform for the agricultural community</li>
          </ul>
        </section>

        <section>
          <h2 className="text-2xl font-semibold text-gray-900 mb-4">LinkedIn Integration</h2>
          <p>When you connect your LinkedIn account:</p>
          <ul className="list-disc pl-6 mt-2 space-y-2">
            <li>We access your basic profile information</li>
            <li>We may sync your professional experience</li>
            <li>We import LinkedIn Learning certificates</li>
            <li>All LinkedIn data remains under your control</li>
          </ul>
        </section>

        <section>
          <h2 className="text-2xl font-semibold text-gray-900 mb-4">Data Protection</h2>
          <p>We protect your agricultural professional data through:</p>
          <ul className="list-disc pl-6 mt-2 space-y-2">
            <li>Secure encrypted connections</li>
            <li>Limited access to authorized personnel only</li>
            <li>Regular security audits</li>
            <li>Compliance with data protection regulations</li>
          </ul>
        </section>

        <section>
          <h2 className="text-2xl font-semibold text-gray-900 mb-4">Contact Us</h2>
          <p>For privacy questions or concerns, contact us at:</p>
          <div className="mt-2">
            <p><strong>SharkNo Agricultural Network</strong></p>
            <p>Email: privacy@sharkno-agricultural.com</p>
            <p>Platform: SharkNo Agricultural Professional Network</p>
          </div>
        </section>

        <section className="bg-green-50 p-4 rounded-lg">
          <h3 className="text-lg font-semibold text-green-900 mb-2">🌾 Agricultural Community Focus</h3>
          <p className="text-green-800">
            Our platform is designed specifically for agricultural professionals. We understand the unique needs 
            of farmers, consultants, veterinarians, agronomists, and agricultural suppliers. Your professional 
            agricultural data helps build a trusted network for the farming community.
          </p>
        </section>
      </div>

      <div className="mt-8 text-sm text-gray-500">
        <p>Last updated: {new Date().toLocaleDateString()}</p>
        <p>Effective for SharkNo Agricultural Network development version</p>
      </div>
    </div>
  );
};

export default Privacy;