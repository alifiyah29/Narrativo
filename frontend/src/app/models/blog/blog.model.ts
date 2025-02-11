export interface Blog {
    id: number;
    title: string;
    content: string;
    visibility: Visibility; // Use enum
    author: {
      id: number;
      username: string;
    };
    createdAt: string;
    updatedAt: string;
    views: number;
  }
  
  export enum Visibility {
    PUBLIC = 'PUBLIC',
    PRIVATE = 'PRIVATE',
  }