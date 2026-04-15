// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebarsApi = {
  apiSidebar: [
    {
      type: 'category',
      label: 'REST API',
      items: [
        'rest/overview',
        'rest/auth',
        'rest/agent',
        'rest/task',
        'rest/code',
        'rest/skill',
        'rest/channel',
      ],
    },
    {
      type: 'category',
      label: 'SDK',
      items: [
        'sdk/java',
        'sdk/python',
        'sdk/typescript',
      ],
    },
    {
      type: 'category',
      label: 'CLI',
      items: [
        'cli/overview',
        'cli/commands',
        'cli/config',
      ],
    },
    {
      type: 'doc',
      label: '错误码',
      id: 'errors',
    },
    {
      type: 'doc',
      label: '速率限制',
      id: 'rate-limits',
    },
  ],
};

module.exports = sidebarsApi;
