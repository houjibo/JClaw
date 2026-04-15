// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  tutorialSidebar: [
    {
      type: 'category',
      label: '入门',
      collapsed: false,
      items: [
        '01-intro',
        '02-quickstart',
        '03-concepts',
      ],
    },
    {
      type: 'category',
      label: '安装配置',
      items: [
        'install/requirements',
        'install/download',
        'install/configure',
        'install/verify',
      ],
    },
    {
      type: 'category',
      label: '基础使用',
      items: [
        'basic/commands',
        'basic/tools',
        'basic/skills',
        'basic/memory',
      ],
    },
    {
      type: 'category',
      label: '高级功能',
      items: [
        'advanced/agents',
        'advanced/mcp',
        'advanced/intent',
        'advanced/collaboration',
      ],
    },
    {
      type: 'doc',
      label: 'FAQ',
      id: 'faq',
    },
    {
      type: 'doc',
      label: '故障排除',
      id: 'troubleshooting',
    },
  ],
};

module.exports = sidebars;
