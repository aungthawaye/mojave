import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

export default defineConfig({
  site: 'https://mojave-docs.local',
  integrations: [
    starlight({
      title: 'Mojave',
      // Starlight >=0.33 expects a single string for favicon
      favicon: '/favicon.svg',
      // Logo can be a light/dark pair directly (use project-relative paths so Starlight can resolve them)
      logo: { light: './src/assets/logo.svg', dark: './src/assets/logo-dark.svg', alt: 'Mojave' },
      // Starlight >=0.33 expects an array for social links

      social: [
        { icon: 'github', label: 'GitHub', href: 'https://github.com/your-org/mojave' },
        { icon: 'x.com', label: 'X (Twitter)', href: 'https://x.com/your_handle' }
      ],
      sidebar: [
        { label: 'Overview', link: '/docs/' },
        { label: 'Getting Started', link: '/docs/getting-started/' },
        { label: 'Guides', autogenerate: { directory: 'docs/guides' } }
      ],
      components: {
        // place for custom overrides if needed later
      },
      editLink: {
        baseUrl: 'https://github.com/your-org/mojave/edit/main/documentation/astro'
      }
    })
  ],
});
