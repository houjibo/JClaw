// @ts-check
// JClaw 文档站点配置

const { themes } = require('prism-react-renderer');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'JClaw',
  tagline: '下一代 AI 编码助手',
  favicon: 'img/favicon.ico',

  // 设置生产部署 URL
  url: 'https://docs.jclaw.dev',
  baseUrl: '/',

  // GitHub Pages 部署配置
  organizationName: 'houjibo',
  projectName: 'JClaw',
  deploymentBranch: 'gh-pages',
  trailingSlash: false,

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // 国际化
  i18n: {
    defaultLocale: 'zh-CN',
    locales: ['zh-CN', 'en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/houjibo/JClaw/tree/main/docs-site/',
          showLastUpdateTime: true,
          showLastUpdateAuthor: true,
        },
        blog: {
          showReadingTime: true,
          editUrl: 'https://github.com/houjibo/JClaw/tree/main/docs-site/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      // 导航栏
      navbar: {
        title: 'JClaw',
        logo: {
          alt: 'JClaw Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'tutorialSidebar',
            position: 'left',
            label: '文档',
          },
          {
            to: '/guides',
            label: '指南',
            position: 'left',
          },
          {
            to: '/api',
            label: 'API',
            position: 'left',
          },
          {
            to: '/examples',
            label: '示例',
            position: 'left',
          },
          {
            href: 'https://github.com/houjibo/JClaw',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },

      // 页脚
      footer: {
        style: 'dark',
        links: [
          {
            title: '文档',
            items: [
              {
                label: '快速开始',
                to: '/docs/quickstart',
              },
              {
                label: '核心概念',
                to: '/docs/concepts',
              },
              {
                label: '使用指南',
                to: '/guides',
              },
            ],
          },
          {
            title: '社区',
            items: [
              {
                label: 'GitHub Discussions',
                href: 'https://github.com/houjibo/JClaw/discussions',
              },
              {
                label: 'Discord',
                href: 'https://discord.gg/jclaw',
              },
              {
                label: '飞书群',
                href: 'https://applink.feishu.cn/client/chat/chatter/add_by_link?link=xxx',
              },
            ],
          },
          {
            title: '更多',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/houjibo/JClaw',
              },
              {
                label: '技能市场',
                href: 'https://skills.jclaw.dev',
              },
              {
                label: '博客',
                to: '/blog',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} JClaw. Built with Docusaurus.`,
      },

      // Prism 主题
      prism: {
        theme: themes.github,
        darkTheme: themes.dracula,
        additionalLanguages: ['java', 'bash', 'json', 'yaml', 'typescript'],
      },

      // 颜色模式
      colorMode: {
        defaultMode: 'light',
        disableSwitch: false,
        respectPrefersColorScheme: true,
      },

      // 元标签
      metadata: [
        { name: 'keywords', content: 'AI, 编码助手，Java, 智能体，MCP, 自动化' },
        { name: 'description', content: 'JClaw 是下一代 AI 编码助手，基于智谱 GLM 4.7，提供代码理解、生成、优化、测试等全方位能力' },
      ],

      // 公告栏
      announcementBar: {
        id: 'support_us',
        content:
          '🎉 JClaw 1.0.0 正式发布！<a target="_blank" href="https://github.com/houjibo/JClaw">立即体验</a>',
        backgroundColor: '#fafbfc',
        textColor: '#091E42',
        isCloseable: true,
      },
    }),

  // 插件
  plugins: [
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'guides',
        path: 'guides',
        routeBasePath: 'guides',
        sidebarPath: require.resolve('./sidebars-guides.js'),
      },
    ],
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'api',
        path: 'api',
        routeBasePath: 'api',
        sidebarPath: require.resolve('./sidebars-api.js'),
      },
    ],
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'examples',
        path: 'examples',
        routeBasePath: 'examples',
        sidebarPath: require.resolve('./sidebars-examples.js'),
      },
    ],
  ],
};

module.exports = config;
