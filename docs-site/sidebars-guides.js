// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebarsGuides = {
  guidesSidebar: [
    {
      type: 'category',
      label: '技能开发',
      items: [
        'skills/intro',
        'skills/structure',
        'skills/develop',
        'skills/test',
        'skills/publish',
      ],
    },
    {
      type: 'category',
      label: 'Agent 配置',
      items: [
        'agents/intro',
        'agents/roles',
        'agents/coordinator',
        'agents/custom',
      ],
    },
    {
      type: 'category',
      label: '工具使用',
      items: [
        'tools/intro',
        'tools/file',
        'tools/search',
        'tools/git',
        'tools/web',
        'tools/code',
      ],
    },
    {
      type: 'category',
      label: '通道集成',
      items: [
        'channels/intro',
        'channels/telegram',
        'channels/discord',
        'channels/feishu',
        'channels/custom',
      ],
    },
    {
      type: 'category',
      label: 'MCP 集成',
      items: [
        'mcp/intro',
        'setup',
        'resources',
        'tools',
        'custom-server',
      ],
    },
    {
      type: 'category',
      label: '最佳实践',
      items: [
        'best-practices/prompt',
        'best-practices/security',
        'best-practices/performance',
        'best-practices/testing',
      ],
    },
  ],
};

module.exports = sidebarsGuides;
